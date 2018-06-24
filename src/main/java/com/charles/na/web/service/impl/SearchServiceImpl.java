package com.charles.na.web.service.impl;

import com.charles.na.common.EventConstant;
import com.charles.na.mapper.EventMapper;
import com.charles.na.model.Event;
import com.charles.na.service.IVectorService;
import com.charles.na.utils.DocumentVector2MapUtil;
import com.charles.na.utils.RedisUtil;
import com.charles.na.web.service.ISearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.util.CollectionsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;


/**
 * @author huqj
 */
@Service("searchService")
public class SearchServiceImpl implements ISearchService {

    private Logger LOGGER = Logger.getLogger(SearchServiceImpl.class);

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.pass}")
    private String redisPass;

    private RedisUtil redisUtil;

    @Autowired
    private IVectorService vectorService;

    @Autowired
    private EventMapper eventMapper;

    private Map<String, List<Event>> reverseDocMap = new HashMap<String, List<Event>>();

    private long Interval = 24 * 3600 * 1000;  //倒排文档的更新时间

    @PostConstruct
    public void init() {
        //初始化redis操作工具
        redisUtil = new RedisUtil(redisHost, redisPort, redisPass);

    }

    //倒排文档实现
    private void reverseDocument() {

    }

    public List<Event> searchEvent(String keywords) {
        Map<String, Double> keys = splitKeywords(keywords);
        Set<String> words = keys.keySet();
        List<Event> eventList = eventMapper.findAll();
        Iterator<Event> iterator = eventList.iterator();
        while (iterator.hasNext()) {
            Event next = iterator.next();
            List<String> ws = splitKeywords2List(next.getTitle());
            boolean del = true;
            for (String s : ws) {
                if (words.contains(s)) {
                    del = false;
                    break;
                }
            }
            if (del) {
                iterator.remove();
            }
        }
        if (CollectionUtils.isEmpty(eventList)) {
            return null;
        }
        List<Pair<Event, Double>> list = new ArrayList<Pair<Event, Double>>();
        for (Event e : eventList) {
            Map<String, Double> emap = splitKeywords(e.getTitle());
            double sim = vectorService.calSimilarityBetweenMap(emap, keys);
            Pair<Event, Double> p = new Pair<Event, Double>(e, sim);
            list.add(p);
        }
        Collections.sort(list, new Comparator<Pair<Event, Double>>() {
            public int compare(Pair<Event, Double> o1, Pair<Event, Double> o2) {
                return (int) (o2.getValue() - o1.getValue());
            }
        });
        eventList.clear();
        for (Pair<Event, Double> p : list) {
            Event e = p.getKey();
            ObjectMapper objectMapper = new ObjectMapper();
            String curHot = "0";
            String lastUpdate = "";
            String tend = "-";
            try {
                LinkedHashMap<String, Integer> map = objectMapper.readValue(e.getHot(), LinkedHashMap.class);
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    if (!lastUpdate.equals("")) {
                        int hotNum = Integer.parseInt(curHot);
                        if (entry.getValue() > hotNum) {
                            tend = "↑";
                        } else if (entry.getValue() < hotNum) {
                            tend = "↓";
                        }
                    }
                    lastUpdate = entry.getKey();
                    curHot = String.valueOf(entry.getValue() * 2);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                LOGGER.error("反解析热度出错。");
            }
            //一个字段多个用途,这里带些信息去查询结果页面
            e.setHot(curHot);
            e.setSentiment(lastUpdate);
            e.setPeople(tend);
            eventList.add(e);
        }
        return eventList;
    }

    private Map<String, Double> splitKeywords(String keys) {
        if (StringUtils.isEmpty(keys)) {
            return null;
        }
        List<String> result = new ArrayList<String>();
        Result res = BaseAnalysis.parse(keys);
        Iterator<Term> iterator = res.iterator();
        while (iterator.hasNext()) {
            Term t = iterator.next();
            String name = t.getName();
            result.add(name);
        }
        Map<String, Double> map = new HashMap<String, Double>();
        for (String s : result) {
            if (map.get(s) == null) {
                map.put(s, 1.0);
            } else {
                map.put(s, map.get(s) + 1);
            }
        }
        return map;
    }

    private List<String> splitKeywords2List(String keys) {
        if (StringUtils.isEmpty(keys)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<String>();
        Result res = BaseAnalysis.parse(keys);
        Iterator<Term> iterator = res.iterator();
        while (iterator.hasNext()) {
            Term t = iterator.next();
            String name = t.getName();
            result.add(name);
        }
        return result;
    }
}
