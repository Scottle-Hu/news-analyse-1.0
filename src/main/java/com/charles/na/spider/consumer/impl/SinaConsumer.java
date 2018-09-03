package com.charles.na.spider.consumer.impl;

import com.charles.na.mapper.NewsMapper;
import com.charles.na.model.News;
import com.charles.na.spider.consumer.ConsumerSpider;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * 实现新浪页面的抓取和解析
 *
 * @author Charles
 */
@Log4j
@Service("sinaConsumer")
public class SinaConsumer extends ConsumerSpider {

    @Autowired
    private NewsMapper newsMapper;

    /**
     * 抓取网页内容并解析
     *
     * @param b
     */
    @Override
    public void accept(byte[] b) {
        try {
            //从消费者获取的byte流构造字符串
            String link = new String(b, "utf-8");
            System.out.println(link);   //for test
//            //构造新闻
//            News news = buildNewsFromUrl(link);
//            //插入新闻
//            newsMapper.insert(news);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从url分析页面，构造新闻对象
     *
     * @param url
     * @return
     */
    private News buildNewsFromUrl(String url) {
        return null;
    }

}
