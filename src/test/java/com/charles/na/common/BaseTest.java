package com.charles.na.common;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by Administrator on 2018/3/28 0028.
 */
public class BaseTest {

    public BaseTest() {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        ac.start();
    }

    @Test
    public void test01() throws IOException {
        BaseTest bt = new BaseTest();
    }
}
