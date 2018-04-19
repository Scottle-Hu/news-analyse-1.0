package com.charles.na.service.impl;

import com.charles.na.service.IClusterService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author huqj
 * @create 2018/4/19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class ClusterServiceImplTest {

    @Autowired
    private IClusterService clusterService;

    @Test
    public void test01() {
        clusterService.canopy();
    }

}