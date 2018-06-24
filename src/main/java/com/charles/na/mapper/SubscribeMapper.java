package com.charles.na.mapper;

import com.charles.na.web.model.Subscribe;

import java.util.List;

/**
 * 事件订阅的数据库处理
 *
 * @author huqj
 */
public interface SubscribeMapper {

    /**
     * 查询某个用户的全部订阅
     *
     * @param userId
     * @return
     */
    List<Subscribe> findEventsIdByUserId(String userId);

    /**
     * 存储一个订阅关系
     *
     * @param subscribe
     * @return
     */
    int insertOne(Subscribe subscribe);
}
