package com.charles.na.web.service.impl;

import com.charles.na.model.EventResult;
import com.charles.na.utils.TimeUtil;
import com.charles.na.web.model.MainPageInfo;
import com.charles.na.web.service.IQueryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.mapreduce.jobhistory.EventReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class QueryServiceImplTest {

    @Autowired
    private IQueryService queryService;

    @Test
    public void testQueryByDuring() throws JsonProcessingException {
        long start = System.currentTimeMillis();
        MainPageInfo mainPageInfo = queryService.queryByDuring("2018-06-10", "2018-06-10");
        System.out.println("查询耗时：" + TimeUtil.convertMillis2String(System.currentTimeMillis() - start));
        System.out.println(mainPageInfo);
        System.out.println(new ObjectMapper().writeValueAsString(mainPageInfo));
    }

    @Test
    public void testFindEventResultByDateAndId() throws JsonProcessingException {
        long start = System.currentTimeMillis();
        EventResult result = queryService.findEventResultByDateAndId("2018-06-14", "1528733767610245");
        System.out.println("查询耗时：" + TimeUtil.convertMillis2String(System.currentTimeMillis() - start));
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    public void testGetDateListByDuring() {
        List<String> res = getDateListByDuring("2018-06-15", "2018-06-17");
        System.out.println("2018-06-15 -> 2018-06-17");
        for (String s : res) {
            System.out.println(s);
        }
        res = getDateListByDuring("2018-06-15", "2018-06-15");
        System.out.println("2018-06-15 -> 2018-06-15");
        for (String s : res) {
            System.out.println(s);
        }
        res = getDateListByDuring("2018-06-15", "2018-06-14");
        System.out.println("2018-06-15 -> 2018-06-14");
        for (String s : res) {
            System.out.println(s);
        }
    }

    @Test
    public void testPreDate() {
        String date = "2018-06-12";
        String pre = getPreDate(date);
        System.out.println(pre);
        assert pre.equals("2018-06-11");
    }

    private List<String> getDateListByDuring(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long startTime = -1, endTime = -1;
        try {
            startTime = sdf.parse(start).getTime();
            endTime = sdf.parse(end).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        if (startTime > endTime) {
            return Collections.emptyList();
        }
        List<String> dates = new ArrayList<String>();
        while (endTime >= startTime && endTime != -1 && startTime != -1) {
            String day = sdf.format(new Date(endTime));
            dates.add(day);
            endTime -= 24 * 3600 * 1000;
        }
        return dates;
    }

    private String getPreDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long startTime = -1, endTime = -1;
        try {
            endTime = sdf.parse(date).getTime();
            startTime = endTime - 24 * 3600 * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        if (startTime != -1) {
            return sdf.format(new Date(startTime));
        }
        return null;
    }

}