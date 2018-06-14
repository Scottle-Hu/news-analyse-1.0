package com.charles.na.mapper;

import com.charles.na.model.Topic;

import java.util.List;

/**
 * @author huqj
 * @create 2018/4/17
 * @since 1.0
 */
public interface TopicMapper {

    /**
     * @param topic
     * @return
     */
    int saveTopic(Topic topic);

    /**
     * 查询指定日期的话题id集合
     *
     * @param date
     * @return
     */
    List<String> findIdsByDate(String date);

    /**
     * 根据id查询topic记录
     *
     * @param id
     * @return
     */
    Topic findById(String id);


}
