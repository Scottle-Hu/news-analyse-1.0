package com.charles.na;

import com.charles.na.service.IResultService;
import com.charles.na.service.ITrackService;
import com.charles.na.soa.INewsSOAService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 定时执行的测试方法
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class CrontabTest {

    @Resource
    private INewsSOAService newsSOAService;

    @Resource
    private ITrackService trackService;

    @Resource
    private IResultService resultService;

    /**
     * 文档向量建立->聚类->提取话题->事件追踪->抽取结果
     */
    @Test
    public void mainTest() throws InterruptedException {
        newsSOAService.vector();
        //创建文档向量需要等待十分钟，因为开了新线程就没管了
        Thread.sleep(600000);
        newsSOAService.cluster();
        newsSOAService.topic();
        trackService.track();
        resultService.convert2result();
    }

}
