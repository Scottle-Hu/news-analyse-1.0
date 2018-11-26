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
@Order(2)
@Service("neteasePlan")
public class NeteaseSpiderPlan extends SpiderPlan {

    @Resource
    private ProducerSpider neteaseProducer;

    @Resource
    private ConsumerSpider neteaseConsumer;

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
        queue = new PriorityQueue(zkUrl, timeout, znode + "/netease");
        setPlanName("NetEasePlan");
    }

    @Override
    public void beginSpider() {
        /*
        注册爬虫消费者
         */
        queue.consume(neteaseConsumer);
        /*
        开启生产者生产数据
         */
        neteaseProducer.produce(queue);
    }

    @Override
    public void setDate(String date) {
        this.neteaseProducer.setDate(date);
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
