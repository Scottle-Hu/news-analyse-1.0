package com.charles.na.service.impl;

import com.charles.na.common.EventConstant;
import com.charles.na.mapper.ClusterMapper;
import com.charles.na.mapper.EventMapper;
import com.charles.na.mapper.TopicMapper;
import com.charles.na.model.*;
import com.charles.na.service.IEventService;
import com.charles.na.service.INewsService;
import com.charles.na.service.ITrackService;
import com.charles.na.service.IVectorService;
import com.charles.na.utils.DocumentVector2MapUtil;
import com.charles.na.utils.IDUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 话题追踪实现类
 *
 * @author huqj
 */
@Service("trackService")
public class TrackServiceImpl implements ITrackService {

    private Logger LOGGER = Logger.getLogger(TrackServiceImpl.class);

    private String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    //进行历史话题追踪的时候往前算的天数
    private int preDays = 3;

    //足够使得两个话题归为一类的最小相似度
    private double SIM_ENOUGH_TOBE_EVENT = 0.4;

    @Resource
    private EventMapper eventMapper;

    @Resource
    private INewsService newsService;

    @Resource
    private TopicMapper topicMapper;

    @Resource
    private ClusterMapper clusterMapper;

    @Resource
    private IVectorService vectorService;

    @Resource
    private IEventService eventService;

    public void track() {
        //获取历史事件
        List<Pair<Event2Topic, Map<String, Double>>> eventHis = new ArrayList<Pair<Event2Topic, Map<String, Double>>>();
        long now = System.currentTimeMillis();
        try {
            now = new SimpleDateFormat("yyyy-MM-dd").parse(today).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < preDays; i++) {
            Date date = new Date(now - (i + 1) * 24 * 3600 * 1000);
            String str = new SimpleDateFormat("yyyy-MM-dd").format(date);
            List<Event2Topic> eventAndTopicList = eventMapper.findEventAndTopicByDate(str);
            Map<Event2Topic, Set<Map<String, Double>>> eventMap = reduceByEventId(eventAndTopicList);
            List<Pair<Event2Topic, Map<String, Double>>> eventHisList = new ArrayList<Pair<Event2Topic, Map<String, Double>>>();
            for (Map.Entry<Event2Topic, Set<Map<String, Double>>> e : eventMap.entrySet()) {
                Map<String, Double> tmp = DocumentVector2MapUtil.calCenterOfVectors(e.getValue());
                eventHisList.add(new ImmutablePair<Event2Topic, Map<String, Double>>(e.getKey(), tmp));
            }
            eventHis.addAll(eventHisList);
        }
        //获取今日所有的热点话题并归类
        List<String> topicIds = topicMapper.findIdsByDate(today);
        for (String id : topicIds) {
            Map<String, Double> centerOfCluster = getCenterOfTopic(id);
            //按事件从近到远归类到事件，若都没有则创建新事件
            boolean hasEvent = false;
            for (Pair<Event2Topic, Map<String, Double>> pair : eventHis) {
                DocumentVector dv1 = DocumentVector2MapUtil.convertMap2DocumentVector(pair.getValue());
                DocumentVector dv2 = DocumentVector2MapUtil.convertMap2DocumentVector(centerOfCluster);
                double sim = vectorService.calSimilarity(dv1, dv2);
                System.out.println("话题相似度：" + sim);
                if (sim >= SIM_ENOUGH_TOBE_EVENT) {
                    //创建 事件-话题 的关联并更新事件相关属性
                    Event2Topic old = pair.getLeft();
                    Event2Topic event2Topic = new Event2Topic();
                    event2Topic.setEventId(old.getEventId());
                    event2Topic.setTopicId(id);
                    event2Topic.setDate(today);
                    eventMapper.saveEvent2Topic(event2Topic);
                    Topic topic = topicMapper.findById(id);
                    Event event = eventMapper.findById(old.getEventId());
                    LOGGER.info("事件归类：" + topic.getTopic() + "->" + event.getTitle());
                    event.setId(event2Topic.getEventId());
                    event.setTitle(topic.getTopic());
                    String eventHot = event.getHot();
                    if (eventHot == null) {
                        eventHot = "";
                    }
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        LinkedMap hotMap = objectMapper.readValue(eventHot, LinkedMap.class);
                        if (hotMap == null) {
                            hotMap = new LinkedMap();
                        }
                        if (hotMap.get(today) != null) {  //有两个话题与该事件相关，热度相加
                            hotMap.put(today, Integer.parseInt(hotMap.get(today).toString()) + topic.getHot());
                        } else {
                            hotMap.put(today, topic.getHot());
                        }
                        eventHot = objectMapper.writeValueAsString(hotMap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    event.setHot(eventHot);
                    //调用方法分析最新话题的情感倾向和热点人物
                    Map<String, String> sentimentAndPeople = eventService.parseSentimentAndPeopleOfTopic(topic.getId());
                    event.setSentiment(sentimentAndPeople.get(EventConstant.SENTIMENT_KEY));
                    event.setPeople(sentimentAndPeople.get(EventConstant.PEOPLE_KEY));
                    eventMapper.update(event);  //更新
                    hasEvent = true;
                    break;
                }
            }
            //创建新事件
            if (!hasEvent) {
                String eventId = IDUtil.generateID();
                Event2Topic event2Topic = new Event2Topic();
                event2Topic.setEventId(eventId);
                event2Topic.setTopicId(id);
                event2Topic.setDate(today);
                eventMapper.saveEvent2Topic(event2Topic);
                Topic topic = topicMapper.findById(id);
                Event event = new Event();
                event.setId(eventId);
                event.setTitle(topic.getTopic());
                ObjectMapper objectMapper = new ObjectMapper();
                LinkedMap hotMap = new LinkedMap();
                hotMap.put(today, topic.getHot());
                String eventHot = "";
                try {
                    eventHot = objectMapper.writeValueAsString(hotMap);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                event.setHot(eventHot);
                //调用方法分析最新话题的情感倾向和热点人物
                Map<String, String> sentimentAndPeople = eventService.parseSentimentAndPeopleOfTopic(topic.getId());
                event.setSentiment(sentimentAndPeople.get(EventConstant.SENTIMENT_KEY));
                event.setPeople(sentimentAndPeople.get(EventConstant.PEOPLE_KEY));
                eventMapper.save(event);  //保存
            }
        }
    }

    public void setDate(String date) {
        this.today = date;
    }

    /**
     * 根据topic计算聚类的中心向量
     *
     * @param topicId
     * @return
     */
    private Map<String, Double> getCenterOfTopic(String topicId) {
        List<Cluster> byClusterId = clusterMapper.findByClusterId(topicId);
        Set<Map<String, Double>> mapSet = new HashSet<Map<String, Double>>();
        for (Cluster cluster : byClusterId) {
            Map<String, Integer> tmp = null;
            try {
                tmp = newsService.splitById(cluster.getNewsId());
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("分词建立文档向量失败");
            }
            Map<String, Double> tmp2 = new HashMap<String, Double>();
            for (Map.Entry<String, Integer> e : tmp.entrySet()) {
                tmp2.put(e.getKey(), e.getValue() * 1.0);
            }
            mapSet.add(tmp2);
        }
        //聚类中心的向量，用于计算与历史事件之间的相似度
        Map<String, Double> centerOfCluster = DocumentVector2MapUtil.calCenterOfVectors(mapSet);
        return centerOfCluster;
    }

    /**
     * 聚合属于相同事件的话题
     *
     * @param eventAndTopicList
     * @return
     */
    private Map<Event2Topic, Set<Map<String, Double>>> reduceByEventId(List<Event2Topic> eventAndTopicList) {
        Map<Event2Topic, Set<Map<String, Double>>> result = new HashMap<Event2Topic, Set<Map<String, Double>>>();
        List<Event2Topic> eventAndTopicList2 = new ArrayList<Event2Topic>();
        eventAndTopicList2.addAll(eventAndTopicList);
        Iterator<Event2Topic> iterator = eventAndTopicList.iterator();
        while (iterator.hasNext()) {
            Event2Topic event2Topic = iterator.next();
            Iterator<Event2Topic> iterator2 = eventAndTopicList2.iterator();
            while (iterator2.hasNext()) {
                Event2Topic event2Topic2 = iterator2.next();
                if (event2Topic2.getEventId().equals(event2Topic.getEventId())) {
                    Map<String, Double> vector = getCenterOfTopic(event2Topic2.getTopicId());
                    if (result.get(event2Topic) == null) {
                        result.put(event2Topic, new HashSet<Map<String, Double>>());
                    }
                    result.get(event2Topic).add(vector);
                    iterator2.remove();
                }
            }
            iterator.remove();
        }
        return result;
    }
}
