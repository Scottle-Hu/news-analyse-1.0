package com.charles.na.service.impl;

import com.charles.na.service.IPersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class PersonServiceImplTest {

    @Resource
    private IPersonService personService;

    @Test
    public void testPersonService() {
        System.out.println(personService.isFamousPerson("习近平"));
        System.out.println(personService.isFamousPerson("苏宁"));
    }

}