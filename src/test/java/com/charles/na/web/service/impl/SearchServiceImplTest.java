package com.charles.na.web.service.impl;

import com.charles.na.model.Event;
import com.charles.na.utils.TimeUtil;
import com.charles.na.web.service.ISearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class SearchServiceImplTest {

    @Autowired
    private ISearchService searchService;

    @Test
    public void testSearch() {
        long start = System.currentTimeMillis();
        List<Event> eventList = searchService.searchEvent("世界杯特朗普");
        System.out.println("搜索耗时：" + TimeUtil.convertMillis2String(System.currentTimeMillis() - start));
        if (eventList != null) {
            for (Event e : eventList) {
                System.out.println(e.getTitle());
            }
        }
    }

}