package com.charles.na.spider.producer.impl;

import com.charles.na.spider.ds.PriorityQueue;
import com.charles.na.spider.producer.ProducerSpider;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author Charles
 */
@Service("sinaProducer")
public class SinaProducer implements ProducerSpider {

    private String rootUrl = "http://news.sina.com";

    /**
     * 已经抓取过的url列表
     */
    private Set<String> visitedUrlSet = new HashSet<>();

    /**
     * 待抓取链接
     */
    private Queue<String> toVisitUrlList = new LinkedList<>();

    /**
     * 一次最多推送的新闻页面个数
     */
    private int MAX_ARTICLE_NUM_ONCE = 1000;

    /**
     * 开始收集当天新浪新闻的数据并将待抓取url推到zookeeper
     * 负责 url解析、链接去重 等工作
     *
     * @param queue
     */
    @Override
    public void produce(PriorityQueue queue) {
        int pushArticleNum = 0;  //已经推送到zk的最终新闻页面个数
        toVisitUrlList.offer(rootUrl);
        while (pushArticleNum < MAX_ARTICLE_NUM_ONCE && !toVisitUrlList.isEmpty()) {
            String url = toVisitUrlList.poll();
            if (visitedUrlSet.contains(url)) {
                continue;
            }
            visitedUrlSet.add(url);
            if (isFinalNewsPage(url)) {
                queue.produce(url.getBytes(), 1);  //将最终页面的url推送给消费者
            }
            List<String> links = extractSinaUrl(url);
            if (!CollectionUtils.isEmpty(links)) {
                links.forEach(link -> {
                    if (!visitedUrlSet.contains(link)) {
                        toVisitUrlList.offer(link);
                    }
                });
            }
        }
    }

    /**
     * 该页面是否为最终新闻页面，根据url形式判断
     *
     * @param url
     * @return
     */
    private boolean isFinalNewsPage(String url) {
        return false;
    }

    /**
     * 提取出该页面的站内链接
     *
     * @param url
     * @return
     */
    private List<String> extractSinaUrl(String url) {
        return Collections.emptyList();
    }
    
}
