package com.charles.na.service;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 分词服务接口
 * @since 1.0
 */
public interface INewsSOAService {

    /**
     * 新闻内容分词
     */
    void split();

    /**
     * 新闻文本聚类
     */
    void cluster();

}
