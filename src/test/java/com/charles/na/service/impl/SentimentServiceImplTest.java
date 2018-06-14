package com.charles.na.service.impl;

import com.charles.na.service.ISentimentService;
import com.charles.na.utils.HttpUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class SentimentServiceImplTest {

    @Resource
    private ISentimentService sentimentService;

    @Test
    public void testParseSentiment() {
        System.out.println(sentimentService.parseSentiment("123你好"));
    }
}