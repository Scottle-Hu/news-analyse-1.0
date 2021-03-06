package com.charles.na.soa;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 分词服务接口
 * @since 1.0
 */
public interface INewsSOAService {

    /**
     * 新闻内容分词，建立文本向量
     */
    boolean vector();

    /**
     * 新闻文本聚类
     */
    void cluster();

    /**
     * 提取聚类的关键短语并持久化到数据库
     */
    void topic();

    /**
     * 设置日期
     *
     * @param date
     */
    void setDate(String date);

}
