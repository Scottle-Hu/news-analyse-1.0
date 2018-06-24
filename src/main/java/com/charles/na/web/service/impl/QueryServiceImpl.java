package com.charles.na.web.service.impl;

import com.charles.na.model.EventResult;
import com.charles.na.model.News;
import com.charles.na.service.ISentimentService;
import com.charles.na.utils.MongoUtil;
import com.charles.na.web.model.MainPageInfo;
import com.charles.na.web.model.NameAndValue;
import com.charles.na.web.service.IQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author huqj
 */
@Service("queryService")
public class QueryServiceImpl implements IQueryService {

    private Logger LOGGER = Logger.getLogger(QueryServiceImpl.class);

    @Value("${mongo.host}")
    private String mongoHost;

    @Value("${mongo.port}")
    private int mongoPort;

    @Value("${mongo.dbname}")
    private String dbName;

    @Value("${mongo.collection}")
    private String collectionName;

    private MongoDatabase mongoDatabase;

    private int MAX_PRE_DAYS = 20;

    @Autowired
    private ISentimentService sentimentService;

    @PostConstruct
    public void init() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("host", mongoHost);
        param.put("port", mongoPort);
        param.put("databaseName", dbName);
        MongoUtil mongoUtil = new MongoUtil(param);
        mongoDatabase = mongoUtil.getMongoDatabase();
    }

    public MainPageInfo queryByDuring(String start, String end) {
        List<EventResult> queryEventResult = new ArrayList<EventResult>();
        List<String> dateList = getDateListByDuring(start, end);
        if (CollectionUtils.isEmpty(dateList)) {
            return null;
        }
        List<FindIterable<Document>> results = new ArrayList<FindIterable<Document>>();
        for (String date : dateList) {  //日期是从后往前排序的
            results.add(findResultByDateList(date));
        }
        List<Document> firstDayList = new ArrayList<Document>();
        int i = 1;
        do {
            FindIterable<Document> last = results.get(results.size() - i);
            MongoCursor<Document> iterator = last.iterator();
            while (iterator.hasNext()) {
                firstDayList.add(iterator.next());
            }
            i++;
        } while (CollectionUtils.isEmpty(firstDayList) && i <= results.size());
        if (CollectionUtils.isEmpty(firstDayList)) {
            LOGGER.error("该时间段内没有热点：" + start + "-" + end);
            return null;
        }
        String firstEventList = (String) firstDayList.get(0).get("events");
        List<EventResult> firstList = null;
        try {
            firstList = parseResultList(firstEventList);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.info("反解析事件列表的json出错.");
        }
        if (CollectionUtils.isEmpty(firstList)) {
            LOGGER.info("反解析事件列表的json出错.没有第一天的事件列表.");
            return null;
        }
        Set<String> ids = new HashSet<String>();
        for (FindIterable<Document> finds : results) {
            MongoCursor<Document> iterator = finds.iterator();
            while (iterator.hasNext()) {
                Document d = iterator.next();
                String events = (String) d.get("events");
                List<EventResult> eventList = null;
                try {
                    eventList = parseResultList(events);
                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.info("反解析事件列表的json出错.");
                }
                for (EventResult er : eventList) {
                    String id = er.getId();
                    if (ids.contains(id)) {
                        continue;
                    }
                    ids.add(id);
                    for (EventResult erBefore : firstList) {
                        if (erBefore.getId().equals(id)) {
                            er.setCurHot(er.getCurHot() - erBefore.getCurHot());
                            break;
                        }
                    }
                    queryEventResult.add(er);
                }
            }
        }
        Collections.sort(queryEventResult, new Comparator<EventResult>() {
            public int compare(EventResult o1, EventResult o2) {
                if (o1 == null || o2 == null)
                    return 0;
                return o2.getCurHot() - o1.getCurHot();
            }
        });
        MainPageInfo mainPageInfo = new MainPageInfo();
        //分析负面新闻
        ObjectMapper objectMapper = new ObjectMapper();
        List<NameAndValue> negativeList = new ArrayList<NameAndValue>();
        for (EventResult e : queryEventResult) {
            int score = 0;
            try {
                Map<String, String> sentiment = objectMapper.readValue(e.getSentiment(), Map.class);
                score = sentimentService.calNegative(sentiment);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            NameAndValue nv = new NameAndValue();
            nv.setName(e.getTitle());
            nv.setValue(String.valueOf(score) + "-" + e.getId());  //带上事件的Id,方便进入详情页面
            negativeList.add(nv);
        }
        Collections.sort(negativeList, new Comparator<NameAndValue>() {
            public int compare(NameAndValue o1, NameAndValue o2) {
                return Integer.valueOf(o2.getValue().substring(0, o2.getValue().indexOf("-")))
                        - Integer.valueOf(o1.getValue().substring(0, o1.getValue().indexOf("-")));
            }
        });
        if (negativeList.size() > 8) {
            negativeList = negativeList.subList(0, 8);
        }
        mainPageInfo.setNegativeEvent(negativeList);
        if (queryEventResult.size() > 15) {
            queryEventResult = queryEventResult.subList(0, 15);
        }
        //分析人名
        Map<String, Integer> people = new HashMap<String, Integer>();
        for (EventResult e : queryEventResult) {
            try {
                Map<String, Integer> map = objectMapper.readValue(e.getPeople(), Map.class);
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    if (people.get(entry.getKey()) == null) {
                        people.put(entry.getKey(), entry.getValue());
                    } else {
                        people.put(entry.getKey(), people.get(entry.getKey()) + entry.getValue());
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                LOGGER.error("反解析人名热度的时候出错");
            }

        }
        List<NameAndValue> hotPeople = new ArrayList<NameAndValue>();
        for (Map.Entry<String, Integer> e : people.entrySet()) {
            NameAndValue n = new NameAndValue();
            n.setName(e.getKey());
            n.setValue(String.valueOf(e.getValue()));
            hotPeople.add(n);
        }
        mainPageInfo.setHotPeople(hotPeople);
        //分析热点关键词
        List<NameAndValue> hotWords = new ArrayList<NameAndValue>();
        Map<String, Integer> wordsMap = new HashMap<String, Integer>();
        for (EventResult e : queryEventResult) {
            Result res = ToAnalysis.parse(e.getTitle());
            Iterator<Term> iterator = res.iterator();
            while (iterator.hasNext()) {
                Term next = iterator.next();
                String w = next.getName();
                if (w.length() > 1) {
                    if (wordsMap.get(w) == null) {
                        wordsMap.put(w, e.getCurHot());
                    } else {
                        wordsMap.put(w, wordsMap.get(w) + e.getCurHot());
                    }
                }
            }
        }
        for (Map.Entry<String, Integer> e : wordsMap.entrySet()) {
            NameAndValue n = new NameAndValue();
            n.setName(e.getKey());
            n.setValue(String.valueOf(e.getValue()));
            hotWords.add(n);
        }
        mainPageInfo.setHotWords(hotWords);
        //简化首页的数据
        for (EventResult er : queryEventResult) {
            er.setNewsList(Collections.<News>emptyList());
            er.setSentiment(null);
            er.setPeople(null);
            er.setCurHot(er.getCurHot() == 0 ? 1 : er.getCurHot());
        }
        mainPageInfo.setTopEvent(queryEventResult);
        mainPageInfo.setStartDate(start);
        mainPageInfo.setEndDate(end);
        return mainPageInfo;
    }

    public EventResult findEventResultByDateAndId(String date, String id) {
        if (StringUtils.isEmpty(date) || StringUtils.isEmpty(id)) {
            LOGGER.error("date或者id为空。");
            return null;
        }
        EventResult result = new EventResult();
        result.setId(id);
        int i = 0;
        while (i < MAX_PRE_DAYS) {  //最多往前找20天
            i++;
            FindIterable<Document> mongoResult = findResultByDateList(date);
            Document doc = mongoResult.first();
            if (doc == null) {
                date = getPreDate(date);
                continue;
            }
            List<EventResult> events = null;
            try {
                System.out.println(doc.get("events").toString());
                events = parseResultList(doc.get("events").toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (CollectionUtils.isEmpty(events)) {
                date = getPreDate(date);
                continue;
            }
            for (EventResult er : events) {
                if (er.getId().equals(id)) {
                    result = er;
                    result.setViewPoint(date);
                    ObjectMapper objectMapper = new ObjectMapper();
                    LinkedHashMap<String, Integer> hotMap = null;
                    try {
                        hotMap = objectMapper.readValue(result.getHot(), LinkedHashMap.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        LOGGER.info("反解析热点出错");
                    }
                    Map<String, List> hotTend = new HashMap<String, List>();
                    List<String> x = new ArrayList<String>();
                    List<Integer> y = new ArrayList<Integer>();
                    for (Map.Entry<String, Integer> e : hotMap.entrySet()) {
                        x.add(e.getKey());
                        y.add(e.getValue());
                    }
                    hotTend.put("x", x);
                    hotTend.put("y", y);
                    result.setHotTend(hotTend);
                    if (hotMap != null && hotMap.get(date) != null) {
                        result.setCurHot(hotMap.get(date) * 2);
                    } else {
                        result.setCurHot(0);
                    }
                    List<NameAndValue> peopleMap = new ArrayList<NameAndValue>();
                    try {
                        Map<String, Object> peoples = objectMapper.readValue(result.getPeople(), Map.class);
                        if (!CollectionUtils.isEmpty(peoples)) {
                            for (Map.Entry<String, Object> e : peoples.entrySet()) {
                                NameAndValue n = new NameAndValue();
                                n.setName(e.getKey());
                                n.setValue(String.valueOf(e.getValue()));
                                peopleMap.add(n);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    result.setPeopleMap(peopleMap);
                    List<NameAndValue> sentimentMap = new ArrayList<NameAndValue>();
                    try {
                        Map<String, String> sentiments = objectMapper.readValue(result.getSentiment(), Map.class);
                        for (Map.Entry<String, String> e : sentiments.entrySet()) {
                            NameAndValue n = new NameAndValue();
                            n.setName(e.getKey());
                            n.setValue(e.getValue());
                            sentimentMap.add(n);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    result.setSentimentMap(sentimentMap);
                    return result;
                }
            }
            date = getPreDate(date);
        }
        return null;
    }

    private List<String> getDateListByDuring(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long startTime = -1, endTime = -1;
        try {
            startTime = sdf.parse(start).getTime();
            endTime = sdf.parse(end).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            LOGGER.error("逆解析时间出错.");
            return Collections.emptyList();
        }
        if (startTime > endTime) {
            return Collections.emptyList();
        }
        List<String> dates = new ArrayList<String>();
        while (endTime >= startTime) {
            String day = sdf.format(new Date(endTime));
            dates.add(day);
            endTime -= 24 * 3600 * 1000;
        }
        return dates;
    }

    private String getPreDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long startTime = -1, endTime = -1;
        try {
            endTime = sdf.parse(date).getTime();
            startTime = endTime - 24 * 3600 * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
            LOGGER.error("逆解析时间出错.");
            return null;
        }
        if (startTime != -1) {
            return sdf.format(new Date(startTime));
        }
        return null;
    }

    //查询mongo中的结果文档
    private FindIterable<Document> findResultByDateList(String date) {
        FindIterable<Document> result = mongoDatabase.getCollection(collectionName).find(new Document("date", date));
        return result;
    }

    private List<EventResult> parseResultList(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> list = objectMapper.readValue(json, List.class);
        List<EventResult> result = new ArrayList<EventResult>();
        for (Map<String, Object> map : list) {
            EventResult eventResult = new EventResult();
            eventResult.setId(map.get("id").toString());
            eventResult.setTitle(map.get("title").toString());
            int chot = Integer.parseInt(map.get("curHot").toString());
            eventResult.setCurHot(chot);
            eventResult.setHot(map.get("hot").toString());
            eventResult.setPeople(map.get("people").toString());
            eventResult.setSentiment(map.get("sentiment").toString());
            String newsString = map.get("newsList").toString();
            List<News> newsList = process(newsString);
            eventResult.setNewsList(newsList);
            result.add(eventResult);
        }
        return result;
    }

    private List<News> process(String newsString) {
        try {
            String result = newsString;
            List<News> newsList = new ArrayList<News>();
            int index = result.indexOf("{id=");
            while (index != -1) {
                News n = new News();
                int idEnd = result.indexOf(", title=", index);
                String id = result.substring(index + 4, idEnd);
                int titleStart = idEnd;
                int titleEnd = result.indexOf(", time=", titleStart);
                String title = result.substring(titleStart + 8, titleEnd);
                int timeStart = titleEnd;
                int timeEnd = result.indexOf(", source=", timeStart);
                String time = result.substring(timeStart + 8, timeEnd);
                int sourceStart = timeEnd;
                int sourceEnd = result.indexOf(", content=", sourceStart);
                String source = result.substring(sourceStart + 10, sourceEnd);
                int contentStart = sourceEnd;
                int contentEnd = result.indexOf(", keywords", contentStart);
                String content = result.substring(contentStart + 10, contentEnd);
                int urlStart = result.indexOf(", url=", contentEnd);
                int urlEnd = result.indexOf(", catchTime", urlStart);
                String url = result.substring(urlStart + 6, urlEnd);
                n.setId(id);
                n.setTitle(title);
                n.setTime(time);
                n.setSource(source);
                n.setContent(content);
                n.setUrl(url);
                newsList.add(n);
                result = result.substring(index + 2);
                index = result.indexOf("{id=");
            }
            return newsList;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("解析新闻列表出错");
            return null;
        }
    }

}
