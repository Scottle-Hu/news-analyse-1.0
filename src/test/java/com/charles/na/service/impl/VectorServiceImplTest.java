package com.charles.na.service.impl;

import com.charles.na.model.DocumentVector;
import com.charles.na.service.IVectorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by huqj on 2018/4/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class VectorServiceImplTest {

    @Autowired
    private IVectorService vectorService;

    @Test
    public void testCalSimilarity() {
        DocumentVector v1 = new DocumentVector();
        v1.setVector("(hello:1 good:2)");
        DocumentVector v2 = new DocumentVector();
        v2.setVector("(good:1 yes:2)");
        double res = vectorService.calSimilarity(v1, v2);
        System.out.println("相似度计算结果：" + res);
    }

}