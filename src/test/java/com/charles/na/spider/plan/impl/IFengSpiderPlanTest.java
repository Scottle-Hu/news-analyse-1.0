package com.charles.na.spider.plan.impl;

import com.charles.na.spider.plan.SpiderPlan;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author huqj  测试爬虫生产消费队列
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class IFengSpiderPlanTest {

    @Autowired
    private SpiderPlan iFengPlan;

    @Test
    public void testCollect() {
        iFengPlan.beginSpider();
    }

}