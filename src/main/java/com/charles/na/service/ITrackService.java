package com.charles.na.service;

/**
 * 热点事件追踪业务
 *
 * @author huqj
 */
public interface ITrackService {

    /**
     * 将当天话题归入历史事件中，如果不存在则新建事件
     */
    void track();

}
