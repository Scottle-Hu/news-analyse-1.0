package com.charles.na.service.impl;

import com.charles.na.model.DocumentVector;
import com.charles.na.service.IClusterService;
import com.charles.na.utils.TimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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
        long start = System.currentTimeMillis();
        List<DocumentVector> canopy = clusterService.canopyForTest();
        System.out.println("聚类结果：" + canopy);
        System.out.println("聚类个数：" + canopy.size());
        System.out.println("抽样canopy耗时：" + TimeUtil.convertMillis2String(System.currentTimeMillis() - start));
    }

}