package com.charles.na.spider.producer.impl;

import com.charles.na.spider.ds.PriorityQueue;
import com.charles.na.spider.producer.ProducerSpider;
import com.charles.na.spider.service.PushedArticleNumZK;
import com.charles.na.utils.HttpUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 搜狐新闻的爬虫生产者
 *
 * @author huqj
 */
@Service("souhuProducer")
@Log4j
public class SouhuProducer implements ProducerSpider {

    private String rootUrl = "http://news.sohu.com/";

    /**
     * 已经抓取过的url列表
     */
    private Set<String> visitedUrlSet = new HashSet<>();

    /**
     * 待抓取链接，使用优先队列
     */
    private Queue<String> toVisitUrlList = new LinkedList<>();

    /**
     * 已经成功消费的文章数量
     */
    @Autowired
    private PushedArticleNumZK pushedArticleNumZk;

    /**
     * 限制待抓取队列的最大长度
     */
    @Value("${spider.urllist.maxsize}")
    private int MAX_URL_LIST_SIZE;

    /**
     * 一次最多推送的新闻页面个数
     */
    @Value("${spider.batch.num}")
    private int MAX_ARTICLE_NUM_ONCE;

    /**
     * 提供自主设置时间的接口
     */
    private String exactDate;

    private SimpleDateFormat dateFormatNormal = new SimpleDateFormat("yyyy-MM-dd");

    @PostConstruct
    public void init() {
        //定时同步url列表长度，用来作为调整队列长度限制的依据
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("[souhu] toVisitUrlList length: " + toVisitUrlList.size()
                        + ", visitedUrlSet length: " + visitedUrlSet.size());
            }
        }, 0, 60000);
    }

    @Override
    public void produce(PriorityQueue queue) {
        toVisitUrlList.offer(rootUrl);
        while (pushedArticleNumZk.get() < MAX_ARTICLE_NUM_ONCE && !toVisitUrlList.isEmpty()) {
            String url = toVisitUrlList.poll();
            try {
                if (visitedUrlSet.contains(url)) {
                    continue;
                }
                visitedUrlSet.add(url);
                if (isFinalNewsPage(url)) {
                    queue.produce(url.getBytes(), 1);  //将最终页面的url推送给消费者
                }
                List<String> links = extractSinaUrl(url);
                if (!CollectionUtils.isEmpty(links) && toVisitUrlList.size() < MAX_URL_LIST_SIZE) {
                    links.forEach(link -> {
                        toVisitUrlList.offer(link);
                    });
                }
            } catch (Exception e) {
                log.error("[souhu] error when produce url from: " + url, e);
            }
        }
        //队列空了导致没有新闻继续抓取，打个warning
        if (toVisitUrlList.isEmpty()) {
            log.warn("[souhu] toVisitUrlList is empty! Producing stopped!");
        }
    }

    /**
     * 判断该链接是否是最终新闻页面，搜狐新闻无法从url判断日期！！
     *
     * @param url
     * @return
     */
    private boolean isFinalNewsPage(@NotNull String url) {
        return url.startsWith("http://www.sohu.com/a/")
                && url.indexOf("/", url.indexOf("http://www.sohu.com/a/") + 22) == -1;
    }

    /**
     * 提取出该页面的站内链接
     *
     * @param url
     * @return
     */
    private List<String> extractSinaUrl(String url) {
        String content = HttpUtil.getRequest(url, null);
        if (StringUtils.isEmpty(content)) {
            return Collections.emptyList();
        }
        List<String> links = new ArrayList<>();
        int start = content.indexOf("href=\"");
        while (start != -1) {
            int end = content.indexOf("\"", start + 6);
            if (end != -1) {
                String link = content.substring(start + 6, end).trim();
                if ((link.startsWith("http://www.sohu.com") || link.startsWith("https://www.sohu.com"))
                        && !visitedUrlSet.contains(link)) {
                    links.add(link.trim());
                }
            }
            start = content.indexOf("href=\"", start + 6);
        }
        return links.size() > 0 ? links : Collections.emptyList();
    }


    @Override
    public void setDate(String date) {
        this.exactDate = date;
    }

}
