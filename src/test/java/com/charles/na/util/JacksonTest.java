package com.charles.na.util;

import com.charles.na.model.Event;
import com.charles.na.model.EventResult;
import com.charles.na.model.News;
import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

/**
 * @author huqj
 */
public class JacksonTest {

    @Test
    public void testJacksonList() throws IOException {
        List<Event> eventList = new ArrayList<Event>();
        Event e1 = new Event();
        e1.setSentiment("sentiment1");
        e1.setId("1");
        e1.setHot("hot1");
        e1.setTitle("title1");
        e1.setPeople("people1");
        eventList.add(e1);
        Event e2 = new Event();
        e2.setSentiment("sentiment2");
        e2.setId("2");
        e2.setHot("hot2");
        e2.setTitle("title2");
        e2.setPeople("people2");
        eventList.add(e2);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(eventList);
        System.out.println(json);
        //反序列化
        List<Map> list = objectMapper.readValue(json, List.class);
        System.out.println(list.get(0));
        assert list.get(0).get("id").equals("1");
        assert list.get(0).get("title").equals("title1");
        assert list.get(1).get("id").equals("2");
        assert list.get(1).get("title").equals("title2");
    }

    @Test
    public void testJacksonListWithList() throws IOException {
        List<EventResult> eventList = new ArrayList<EventResult>();
        List<News> newsList = new ArrayList<News>();
        News news1 = new News();
        news1.setId("news1");
        newsList.add(news1);
        News news2 = new News();
        news2.setId("news2");
        newsList.add(news2);
        EventResult er1 = new EventResult();
        er1.setTitle("title1");
        er1.setNewsList(newsList);
        eventList.add(er1);
        EventResult er2 = new EventResult();
        er2.setTitle("title2");
        er2.setNewsList(newsList);
        eventList.add(er2);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(eventList);
        System.out.println(json);
        //反序列化
        List<Map> list = objectMapper.readValue(json, List.class);
        System.out.println(list.get(0));
        assert list.get(0).get("title").equals("title1");
        assert list.get(1).get("title").equals("title2");
        assert ((List<Map>) list.get(1).get("newsList")).get(0).get("id").equals("news1");
    }

}
