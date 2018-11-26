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
 * 抓取搜狐新闻的爬虫计划
 *
 * @author Charles
 */
@Order(3)
@Service("souhuPlan")
public class SouhuSpiderPlan extends SpiderPlan {

    @Resource
    private ProducerSpider souhuProducer;

    @Resource
    private ConsumerSpider souhuConsumer;

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
        queue = new PriorityQueue(zkUrl, timeout, znode + "/souhu");
        setPlanName("SouHuPlan");
    }

    @Override
    public void beginSpider() {
        /*
        注册爬虫消费者
         */
        queue.consume(souhuConsumer);
        /*
        开启生产者生产数据
         */
        souhuProducer.produce(queue);
    }

    @Override
    public void setDate(String date) {
        this.souhuProducer.setDate(date);
    }

    @Override
    public void setPlanName(String planName) {
        this.planName = planName;
    }

    @Override
    public String getPlanName() {
        return planName;
    }

}
