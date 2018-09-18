package com.charles.na.spider.producer.impl;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author huqj
 */
public class SinaProducerTest {

    private SinaProducer producer = new SinaProducer();

    @Test
    public void testIsFinalPageUrl() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String url = "http://news.sina.com.cn/w/" + date + "/doc-ihiqtcan0834272.shtml";
        assert callIsFinalPageFunction(url);
        url = "https://news.sina.com.cn/";
        assert !callIsFinalPageFunction(url);
        url = "http://news.sina.com.cn/w/2018-09/idoc-ihiqtcan0834272.shtml";
        assert !callIsFinalPageFunction(url);
        url = "http://news.sina.com.cn/w/2018-09-01/doc-ihiqtcan0834272.shtml";
        assert !callIsFinalPageFunction(url);
    }

    @Test
    public void testExtractSinaUrl() {
        String url = "https://news.sina.com.cn";
        List<String> links = callExtractSinaUrl(url);
        links.forEach(link -> {
            assert link.startsWith("http");
            assert link.contains("news.sina.com.cn");
            assert !link.contains(" ");
        });
    }

    @Test
    public void testIsOldNewsLink() {
        String url = "http://news.sina.com.cn/w/2018-09/idoc-ihiqtcan0834272.shtml";
        assert !callIsOldNewsLink(url);
        url = "http://news.sina.com.cn/w/2018-09-03/doc-ihiqtcan0834272.shtml";
        assert callIsOldNewsLink(url);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        url = "http://news.sina.com.cn/w/" + date + "/doc-ihiqtcan0834272.shtml";
        assert !callIsOldNewsLink(url);
    }

    private boolean callIsFinalPageFunction(String url) {
        Class cls = SinaProducer.class;
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
        Class cls = SinaProducer.class;
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
            Class cls = SinaProducer.class;
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