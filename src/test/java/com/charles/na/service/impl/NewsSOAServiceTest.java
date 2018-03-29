package com.charles.na.service.impl;

import com.charles.na.service.INewsSOAService;
import com.charles.na.service.INewsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class NewsSOAServiceTest {

    @Resource
    private INewsSOAService newsSOAService;

    @Test
    public void test01() throws IOException {
        newsSOAService.split();
    }

}
