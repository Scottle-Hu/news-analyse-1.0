package com.charles.na.mapper;

import com.charles.na.model.Cluster;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * @author huqj
 * @create 2018/4/19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class ClusterMapperTest {

    @Autowired
    private ClusterMapper clusterMapper;

    @Test
    public void test01() {
        List<Cluster> clusterList = new ArrayList<Cluster>();
        Cluster c1 = new Cluster();
        c1.setId("123");
        c1.setNewsId("111");
        c1.setDate("2018-05-25");
        clusterList.add(c1);
        Cluster c2 = new Cluster();
        c2.setId("124");
        c2.setNewsId("111");
        c2.setDate("2018-05-25");
        clusterList.add(c2);
        clusterMapper.insertMany(clusterList);
    }

}