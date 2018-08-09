package com.charles.na.spider.consumer.impl;

import com.charles.na.spider.consumer.ConsumerSpider;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

/**
 * 实现新浪页面的抓取和解析
 *
 * @author Charles
 */
@Log4j
@Service("sinaConsumer")
public class SinaConsumer extends ConsumerSpider {


    /**
     * 抓取网页内容并解析
     *
     * @param b
     */
    @Override
    public void accept(byte[] b) {
        try {
            String link = new String(b, "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
