package com.charles.na.service.impl;

import com.charles.na.model.DocumentVector;
import com.charles.na.service.IClusterService;
import com.charles.na.utils.TimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

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
        Set<String> removeId = new HashSet<String>();
        List<DocumentVector> src = new ArrayList<DocumentVector>();
        //canopy粗聚类
        Map<DocumentVector, Set<DocumentVector>> canopy = clusterService.canopyForTest(removeId, src);
        System.out.println("canopy聚类结果：" + canopy);
        System.out.println("canopy聚类个数：" + canopy.size());
        System.out.println("抽样canopy耗时：" + TimeUtil.convertMillis2String(System.currentTimeMillis() - start));
        //k-means细化聚类
        start = System.currentTimeMillis();
        clusterService.kMeansForTest(removeId, src, canopy);
        System.out.println("抽样k-means耗时：" + TimeUtil.convertMillis2String(System.currentTimeMillis() - start));
    }

}