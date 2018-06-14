package com.charles.na.service.impl;

import com.charles.na.service.ITrackService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class TrackServiceImplTest {

    @Resource
    private ITrackService trackService;

    @Test
    public void testTrack() {
        trackService.track();
    }
}