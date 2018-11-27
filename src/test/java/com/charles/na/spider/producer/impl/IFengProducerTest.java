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
public class IFengProducerTest {

    private IFengProducer producer = new IFengProducer();

    Class cls = IFengProducer.class;

    @Test
    public void testIsFinalPageUrl() {
        String date = new SimpleDateFormat("/yyyyMMdd/").format(new Date());
        String url = "http://news.ifeng.com/a" + date + "60174751_0.shtml";
        assert callIsFinalPageFunction(url);
        url = "http://news.ifeng.com/";
        assert !callIsFinalPageFunction(url);
        url = "http://news.ifeng.com/a/201811/60174751_0.shtml";
        assert !callIsFinalPageFunction(url);
        url = "http://news.ifeng.com/a/20181125/60174751_0.shtml";
        assert !callIsFinalPageFunction(url);
    }

    @Test
    public void testExtractSinaUrl() {
        String url = "http://news.ifeng.com";
        List<String> links = callExtractSinaUrl(url);
        links.forEach(link -> {
            assert link.startsWith("http");
            assert link.contains("news.ifeng.com");
            assert !link.contains(" ");
        });
    }

    @Test
    public void testIsOldNewsLink() {
        String url = "http://news.ifeng.com/a/20181125/60174751_0.shtml";
        assert callIsOldNewsLink(url);
        url = "http://news.ifeng.com/a/201811/60174751_0.shtml";
        assert !callIsOldNewsLink(url);
        String date = new SimpleDateFormat("/yyyyMMdd/").format(new Date());
        url = "http://news.ifeng.com/a" + date + "60174751_0.shtml";
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