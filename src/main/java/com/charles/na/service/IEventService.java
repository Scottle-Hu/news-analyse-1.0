package com.charles.na.service;

import java.util.Map;

/**
 * 热点事件的业务处理
 *
 * @author huqj
 */
public interface IEventService {

    /**
     * 分析某个话题的情感倾向和相关热点人物
     *
     * @param topicId
     * @return
     */
    Map<String, String> parseSentimentAndPeopleOfTopic(String topicId);

}
