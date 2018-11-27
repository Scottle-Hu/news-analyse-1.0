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
public class TencentProducerTest {

    private TencentProducer producer = new TencentProducer();

    Class cls = TencentProducer.class;

    @Test
    public void testIsFinalPageUrl() {
        String date = new SimpleDateFormat("/yyyyMMdd/").format(new Date());
        String url = "https://new.qq.com/omn" + date + "20181127A03GZW.html";
        assert callIsFinalPageFunction(url);
        url = "https://new.qq.com/";
        assert !callIsFinalPageFunction(url);
        url = "https://new.qq.com/omn2/20181127/20181127A03GZW.html";
        assert !callIsFinalPageFunction(url);
        url = "https://new.qq.com/omn/201811/20181127A03GZW.html";
        assert !callIsFinalPageFunction(url);
    }

    @Test
    public void testExtractSinaUrl() {
        String url = "https://news.qq.com";
        List<String> links = callExtractSinaUrl(url);
        links.forEach(link -> {
            System.out.println(link);
            assert link.startsWith("http");
            assert link.contains("new.qq.com") || link.contains("news.qq.com");
            assert !link.contains(" ");
        });
    }

    @Test
    public void testIsOldNewsLink() {
        String url = "https://new.qq.com/omn/20181125/20181127A03GZW.html";
        assert callIsOldNewsLink(url);
        url = "https://new.qq.com/omn/201811/20181127A03GZW.html";
        assert !callIsOldNewsLink(url);
        String date = new SimpleDateFormat("/yyyyMMdd/").format(new Date());
        url = "https://new.qq.com/omn" + date + "20181127A03GZW.html";
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