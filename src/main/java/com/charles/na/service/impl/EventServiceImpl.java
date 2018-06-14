package com.charles.na.service.impl;

import com.charles.na.common.EventConstant;
import com.charles.na.mapper.ClusterMapper;
import com.charles.na.model.Cluster;
import com.charles.na.model.News;
import com.charles.na.service.IEventService;
import com.charles.na.service.INewsService;
import com.charles.na.service.IPersonService;
import com.charles.na.service.ISentimentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 事件业务处理的实现
 *
 * @author huqj
 */
@Service("eventService")
public class EventServiceImpl implements IEventService {

    private Logger LOGGER = Logger.getLogger(EventServiceImpl.class);

    /**
     * 作为热点人物至少出现的次数
     */
    private int MIN_HOT_PEOPLE_DISPLAY_TIME = 5;

    @Resource
    private ISentimentService sentimentService;

    @Resource
    private INewsService newsService;

    @Resource
    private ClusterMapper clusterMapper;

    @Resource
    private IPersonService personService;

    public Map<String, String> parseSentimentAndPeopleOfTopic(String topicId) {
        Map<String, String> result = new HashMap<String, String>();
        StringBuilder content = new StringBuilder();
        Map<String, Integer> peoples = new HashMap<String, Integer>();
        List<Cluster> clusterList = clusterMapper.findByClusterId(topicId);
        for (Cluster cluster : clusterList) {
            News news = newsService.findById(cluster.getNewsId());
            content.append(news.getContent());
        }
        String contentStr = content.toString();
        Result res = ToAnalysis.parse(contentStr);
        Iterator<Term> iterator = res.iterator();
        while (iterator.hasNext()) {
            Term t = iterator.next();
            if (t.getNatureStr().equals(EventConstant.PEOPLE_NAME_NATURE)) {
                String name = t.getName();
                if (peoples.get(name) == null) {
                    peoples.put(name, 1);
                } else {
                    peoples.put(name, peoples.get(name) + 1);
                }
            }
        }
        Iterator<Map.Entry<String, Integer>> iterator1 = peoples.entrySet().iterator();
        while (iterator1.hasNext()) {
            Map.Entry<String, Integer> e = iterator1.next();
            if (e.getValue() < MIN_HOT_PEOPLE_DISPLAY_TIME || !personService.isFamousPerson(e.getKey())) {
                iterator1.remove();
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String people = "";
        try {
            people = objectMapper.writeValueAsString(peoples);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(people);
        result.put(EventConstant.PEOPLE_KEY, people);
        String sentimentJson = sentimentService.parseSentiment(contentStr);
        System.out.println(sentimentJson);
        result.put(EventConstant.SENTIMENT_KEY, sentimentJson);

        return result;
    }
}
