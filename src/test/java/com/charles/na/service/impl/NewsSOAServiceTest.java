package com.charles.na.service.impl;

import com.charles.na.soa.INewsSOAService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class NewsSOAServiceTest {

    @Resource
    private INewsSOAService newsSOAService;

    @Test
    public void test01() throws IOException {
        newsSOAService.vector();
    }

    @Test
    public void testTopic() {
        newsSOAService.topic();
    }

}
