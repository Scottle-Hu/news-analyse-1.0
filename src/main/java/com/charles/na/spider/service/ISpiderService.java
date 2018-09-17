package com.charles.na.spider.service;

/**
 * 数据收集服务
 *
 * @author huqj
 */
public interface ISpiderService {

    /**
     * 开始采集数据
     */
    void collect();

    void setDate(String date);
}
