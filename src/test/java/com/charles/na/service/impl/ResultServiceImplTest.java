package com.charles.na.service.impl;

import com.charles.na.service.IResultService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class ResultServiceImplTest {

    @Autowired
    private IResultService resultService;

    @Test
    public void testResultService() {
        resultService.convert2result();
    }

}