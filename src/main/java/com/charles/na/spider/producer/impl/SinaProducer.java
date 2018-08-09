package com.charles.na.spider.producer.impl;

import com.charles.na.spider.ds.PriorityQueue;
import com.charles.na.spider.producer.ProducerSpider;
import org.springframework.stereotype.Service;

/**
 * @author Charles
 */
@Service("sinaProducer")
public class SinaProducer implements ProducerSpider {

    /**
     * 开始收集当天新浪新闻的数据并将待抓取url推到zookeeper
     * 负责 url解析、链接去重 等工作
     *
     * @param queue
     */
    @Override
    public void produce(PriorityQueue queue) {
        
    }
}
