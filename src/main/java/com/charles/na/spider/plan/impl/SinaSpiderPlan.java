package com.charles.na.spider.plan.impl;

import com.charles.na.spider.consumer.ConsumerSpider;
import com.charles.na.spider.ds.PriorityQueue;
import com.charles.na.spider.plan.SpiderPlan;
import com.charles.na.spider.producer.ProducerSpider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 抓取新浪新闻的爬虫计划
 *
 * @author Charles
 */
@Order(1)
@Service("sinaPlan")
public class SinaSpiderPlan extends SpiderPlan {

    @Resource
    private ProducerSpider sinaProducer;

    @Resource
    private ConsumerSpider sinaConsumer;

    @Value("${zookeeper.url}")
    private String zkUrl;

    @Value("${zookeeper.timeout}")
    private int timeout;

    @Value("${zookeeper.spider.root}")
    private String znode;

    private PriorityQueue queue;

    @PostConstruct
    public void initQueue() {
        //初始化zk消费队列
        queue = new PriorityQueue(zkUrl, timeout, znode + "/sina");
    }

    @Override
    public void beginSpider() {
        /*
        注册爬虫消费者
         */
        queue.consume(sinaConsumer);
        /*
        开启生产者生产数据
         */
        sinaProducer.produce(queue);
    }
}
