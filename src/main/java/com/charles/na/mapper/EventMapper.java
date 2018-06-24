package com.charles.na.mapper;

import com.charles.na.model.Event;
import com.charles.na.model.Event2Topic;

import java.util.List;

/**
 * 有关事件的数据库操作
 *
 * @author huqj
 */
public interface EventMapper {

    /**
     * 根据日期获取当天的事件话题
     *
     * @param date
     * @return
     */
    List<Event2Topic> findEventAndTopicByDate(String date);

    /**
     * 存储一条记录
     *
     * @param e
     * @return
     */
    int saveEvent2Topic(Event2Topic e);

    /**
     * 更新事件
     *
     * @param event
     * @return
     */
    int update(Event event);

    /**
     * 保存新的事件
     *
     * @param event
     * @return
     */
    int save(Event event);

    /**
     * 根据id获取事件
     */
    Event findById(String id);

    /**
     * 获取所有事件
     *
     * @return
     */
    //TODO 先这么做，后面数据量大了不能这样
    List<Event> findAll();

}
