package com.charles.na.spider.consumer;

import java.util.function.Consumer;

/**
 * 消费者爬虫，从zookeeper读取待爬取链接并抓取网页然后分析
 *
 * @author huqj
 */
public abstract class ConsumerSpider implements Consumer<byte[]> {

    /**
     * 此方法继承类无需实现
     *
     * @param after
     * @return
     */
    @Override
    public Consumer<byte[]> andThen(Consumer<? super byte[]> after) {
        return null;
    }
}
