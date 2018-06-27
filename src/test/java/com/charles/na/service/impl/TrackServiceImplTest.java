package com.charles.na.service.impl;

import com.charles.na.service.ITrackService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({"classpath:applicationContext.xml"})
public class TrackServiceImplTest {

    @Resource
    private ITrackService trackService;

    @Test
    public void testTrack() {
        trackService.track();
    }

    @Test
    public void testDateFormat() {
        long time = 0;
        try {
            time = new SimpleDateFormat("yyyy-MM-dd").parse("2018-06-20").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(time);
        Date exp = new Date(time);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(exp));
        assert new SimpleDateFormat("yyyy-MM-dd").format(exp).equals("2018-06-20");

    }
}