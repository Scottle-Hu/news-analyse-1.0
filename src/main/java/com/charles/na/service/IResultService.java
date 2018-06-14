package com.charles.na.service;

/**
 * 将分析结果转化为结果记录的服务
 *
 * @author huqj
 */
public interface IResultService {

    /**
     * 将数据库中当天的事件转化为结果记录的形式存储到mongo中
     */
    void convert2result();


}
