package com.charles.na.spider.producer;

import com.charles.na.spider.ds.PriorityQueue;

/**
 * 生产者爬虫，负责解析网页中的url并推到zk
 *
 * @author huqj
 */
public interface ProducerSpider {

    /**
     * 向队列中生产数据
     *
     * @param queue
     */
    void produce(PriorityQueue queue);

    void setDate(String date);
}
