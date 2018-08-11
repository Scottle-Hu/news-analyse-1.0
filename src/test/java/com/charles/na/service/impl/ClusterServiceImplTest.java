package com.charles.na.service.impl;

import com.charles.na.model.DocumentVector;
import com.charles.na.service.IClusterService;
import com.charles.na.utils.IDUtil;
import com.charles.na.utils.TimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
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
    public void test02() {
        long start = System.currentTimeMillis();
        Set<Long> removeId = new HashSet<Long>();
        //canopy粗聚类
        Map<DocumentVector, Set<DocumentVector>> canopy = clusterService.canopy(removeId);
        //System.out.println("canopy聚类结果：" + canopy);
        System.out.println("canopy聚类个数：" + canopy.size());
        System.out.println("canopy耗时：" + TimeUtil.convertMillis2String(System.currentTimeMillis() - start));
        //k-means细化聚类
        start = System.currentTimeMillis();
        clusterService.kMeans(canopy, removeId);
        System.out.println("k-means耗时：" + TimeUtil.convertMillis2String(System.currentTimeMillis() - start));
    }

    @Test
    public void test03() throws IOException {
        Set<Long> removeId = new HashSet<Long>();
        //canopy粗聚类
        Map<DocumentVector, Set<DocumentVector>> canopy = new HashMap<DocumentVector, Set<DocumentVector>>();
        File f = new File("canopy_result.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String str = null;
        while ((str = br.readLine()) != null) {
            int splitIndex = str.indexOf(":");
            int rightIndex = str.lastIndexOf(":");
            DocumentVector dv = new DocumentVector();
            dv.setId(IDUtil.generateLongID());
            dv.setNewsId(str.substring(0, splitIndex));
            dv.setVector(str.substring(splitIndex + 1, rightIndex));
            canopy.put(dv, new HashSet<DocumentVector>());
        }
        br.close();
        fr.close();
        long start = System.currentTimeMillis();
        clusterService.kMeans(canopy, removeId);
        System.out.println("k-means耗时：" + TimeUtil.convertMillis2String(System.currentTimeMillis() - start));
    }

}