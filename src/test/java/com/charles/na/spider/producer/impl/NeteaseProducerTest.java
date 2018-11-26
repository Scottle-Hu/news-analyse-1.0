package com.charles.na.spider.producer.impl;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/11/26 0026.
 */
public class NeteaseProducerTest {

    private NeteaseProducer producer = new NeteaseProducer();

    Class cls = NeteaseProducer.class;

    @Test
    public void testIsFinalPageUrl() {
        String date = new SimpleDateFormat("/yy/MMdd/").format(new Date());
        String url = "http://news.163.com" + date + "00/E1HVT1H40001875N.html";
        assert callIsFinalPageFunction(url);
        url = "https://news.163.com/";
        assert !callIsFinalPageFunction(url);
        url = "https://news.163.com/18/1125/14/E1HVT1H40001875N.html";
        assert !callIsFinalPageFunction(url);
        url = "https://news.163.com/w/18/1125/14/E1HVT1H40001875N.html";
        assert !callIsFinalPageFunction(url);
    }

    @Test
    public void testExtractSinaUrl() {
        String url = "https://news.163.com";
        List<String> links = callExtractSinaUrl(url);
        links.forEach(link -> {
            assert link.startsWith("http");
            assert link.contains("news.163.com");
            assert !link.contains(" ");
        });
    }

    @Test
    public void testIsOldNewsLink() {
        String url = "https://news.163.com/18/1125/14/E1HVT1H40001875N.html";
        assert callIsOldNewsLink(url);
        url = "https://news.163.com/18/1125/E1HVT1H40001875N.html";
        assert !callIsOldNewsLink(url);
        String date = new SimpleDateFormat("/yy/MMdd/").format(new Date());
        url = "http://news.163.com" + date + "00/E1HVT1H40001875N.html";
        assert !callIsOldNewsLink(url);
    }

    private boolean callIsFinalPageFunction(String url) {
        for (Method method : cls.getDeclaredMethods()) {
            if ("isFinalNewsPage".equals(method.getName())) {
                method.setAccessible(true);
                try {
                    return (boolean) method.invoke(producer, url);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private List<String> callExtractSinaUrl(String url) {
        for (Method method : cls.getDeclaredMethods()) {
            if ("extractSinaUrl".equals(method.getName())) {
                method.setAccessible(true);
                try {
                    return (List<String>) method.invoke(producer, url);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return Collections.emptyList();
    }

    private boolean callIsOldNewsLink(String url) {
        try {
            Method method = cls.getDeclaredMethod("isOldNewsLink", String.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(producer, url);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

}