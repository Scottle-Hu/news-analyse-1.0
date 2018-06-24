package com.charles.na.service.impl;

import com.charles.na.mapper.ClusterMapper;
import com.charles.na.mapper.EventMapper;
import com.charles.na.mapper.NewsMapper;
import com.charles.na.mapper.TopicMapper;
import com.charles.na.model.*;
import com.charles.na.service.IClusterService;
import com.charles.na.service.IEventService;
import com.charles.na.service.IResultService;
import com.charles.na.service.ITrackService;
import com.charles.na.utils.MongoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author huqj
 */
@Service("resultService")
public class ResultServiceImpl implements IResultService {


    private Logger LOGGER = Logger.getLogger(ResultServiceImpl.class);

    @Value("${mongo.host}")
    private String mongoHost;

    @Value("${mongo.port}")
    private int mongoPort;

    @Value("${mongo.dbname}")
    private String dbName;

    @Value("${mongo.collection}")
    private String collectionName;

    private MongoDatabase mongoDatabase;

    @Resource
    private EventMapper eventMapper;

    @Resource
    private ClusterMapper clusterMapper;

    @Resource
    private TopicMapper topicMapper;

    @Resource
    private NewsMapper newsMapper;

    @PostConstruct
    public void init() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("host", mongoHost);
        param.put("port", mongoPort);
        param.put("databaseName", dbName);
        MongoUtil mongoUtil = new MongoUtil(param);
        mongoDatabase = mongoUtil.getMongoDatabase();
    }

    private String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    public void convert2result() {
        if (mongoDatabase == null) {
            LOGGER.error("mongodb连接无效，抽取当天结果失败：" + today);
            return;
        }
        List<Event2Topic> event2TopicList = eventMapper.findEventAndTopicByDate(today);
        if (CollectionUtils.isEmpty(event2TopicList)) {
            LOGGER.error("当天没有事件，这是不应该发生的情况：" + today);
            return;
        }
        List<Event> eventList = new ArrayList<Event>();
        Map<String, List<String>> event2TopicMap = new HashMap<String, List<String>>();
        for (Event2Topic event2Topic : event2TopicList) {  //一举两得
            if (event2TopicMap.get(event2Topic.getEventId()) == null) {
                event2TopicMap.put(event2Topic.getEventId(), new ArrayList<String>());
                Event e = eventMapper.findById(event2Topic.getEventId());
                eventList.add(e);
            }
            event2TopicMap.get(event2Topic.getEventId()).add(event2Topic.getTopicId());
        }
        if (CollectionUtils.isEmpty(eventList)) {
            LOGGER.error("当天没有事件，这是不应该发生的情况：" + today);
            return;
        }
        List<EventResult> eventResults = new ArrayList<EventResult>();
        for (Event event : eventList) {
            String hotJso = event.getHot();
            ObjectMapper objectMapper = new ObjectMapper();
            LinkedHashMap<String, Integer> hotMap = null;
            try {
                hotMap = objectMapper.readValue(hotJso, LinkedHashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("话题热度在反序列化时出现问题。");
            }
            int hot = 0;
            if (!CollectionUtils.isEmpty(hotMap)) {
                for (Map.Entry<String, Integer> e : hotMap.entrySet()) {
                    hot += e.getValue();
                }
            }
            EventResult eventResult = new EventResult();
            eventResult.setId(event.getId());
            eventResult.setHot(event.getHot());
            eventResult.setPeople(event.getPeople());
            eventResult.setSentiment(event.getSentiment());
            eventResult.setTitle(event.getTitle());
            eventResult.setCurHot(hot);
            eventResult.setNewsList(getNewsByEventId(event.getId(), event2TopicMap));
            eventResults.add(eventResult);
        }
        //写入mongo
        Document doc = new Document();
        doc.append("date", today);
        String events = null;
        try {
            events = new ObjectMapper().writeValueAsString(eventResults);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error("将事件数组转化为json出错");
        }
        doc.append("events", events);
        mongoDatabase.getCollection(collectionName).insertOne(doc);
    }

    private List<News> getNewsByEventId(String eventId, Map<String, List<String>> event2TopicMap) {
        List<News> newsList = new ArrayList<News>();
        List<String> topicIds = event2TopicMap.get(eventId);
        if (!CollectionUtils.isEmpty(topicIds)) {
            for (String topicId : topicIds) {
                List<Cluster> clusters = clusterMapper.findByClusterId(topicId);
                if (!CollectionUtils.isEmpty(clusters)) {
                    for (Cluster c : clusters) {
                        News news = newsMapper.findById(c.getNewsId());
                        if (news != null) {
                            String content = news.getContent();
                            content = content.trim().replace("\n", "")
                                    .replace("\t", "").replace("\r", "");
                            if (content.length() > 50) {
                                content = content.substring(0, 50) + "...";
                            }
                            news.setContent(content);
                            newsList.add(news);
                        }
                    }
                }
            }
        }
        if (newsList.size() > 10)  //最多返回10个相关新闻
            newsList = newsList.subList(0, 10);
        return newsList;
    }

    private void testGetCollection() {
        FindIterable<Document> documents = mongoDatabase.getCollection(collectionName).find();
        MongoCursor<Document> iterator = documents.iterator();
        while (iterator.hasNext()) {
            Document next = iterator.next();
            System.out.println(next.get("events"));
        }
    }


}
