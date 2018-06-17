package com.charles.na.service.impl;

import com.charles.na.common.BaseTest;
import com.charles.na.service.INewsService;
import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Word;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class NewsServiceTest {

    @Resource
    private INewsService newsService;

    @Test
    public void test01() throws IOException {
        Map<String, Integer> map = newsService.splitById("ifwnpcnt4308823");
        System.out.println(map);
    }
}
