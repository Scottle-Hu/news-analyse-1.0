package com.charles.na.spider.producer.impl;

import com.charles.na.spider.ds.PriorityQueue;
import com.charles.na.spider.producer.ProducerSpider;
import com.charles.na.spider.service.PushedArticleNumZK;
import com.charles.na.utils.HttpUtil;
import lombok.extern.log4j.Log4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Charles
 */
@Service("sinaProducer")
@Log4j
public class SinaProducer implements ProducerSpider {

    private String rootUrl = "https://news.sina.com.cn/";

    /**
     * 已经抓取过的url列表
     */
    private Set<String> visitedUrlSet = new HashSet<>();

    /**
     * 待抓取链接，使用优先队列
     */
    private Queue<String> toVisitUrlList = new LinkedList<>();

    /**
     * 已经成功消费的文章数量，注意：不能是生产的链接数量，
     * 因为有些链接是无效的，导致实际消费文章比这个数字少
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

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @PostConstruct
    public void init() {
        log.info("init set MAX_ARTICLE_NUM_ONCE = " + MAX_ARTICLE_NUM_ONCE);
        log.info("init set MAX_URL_LIST_SIZE = " + MAX_URL_LIST_SIZE);
        //定时同步url列表长度，用来作为调整队列长度限制的依据
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("[sina] toVisitUrlList length: " + toVisitUrlList.size()
                        + ", visitedUrlSet length: " + visitedUrlSet.size());
            }
        }, 0, 60000);
    }

    /**
     * 开始收集当天新浪新闻的数据并将待抓取url推到zookeeper
     * 负责 url解析、链接去重 等工作
     *
     * @param queue
     */
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
                log.error("[sina] error when produce url from: " + url, e);
            }
        }
        //队列空了导致没有新闻继续抓取，打个warning
        if (toVisitUrlList.isEmpty()) {
            log.warn("[sina] toVisitUrlList is empty! Producing stopped!");
        }
    }

    /**
     * 该页面是否为最终新闻页面，根据url形式判断，
     * 例如http://news.sina.com.cn/w/2018-09-03/doc-ihiqtcan0834272.shtml
     * 判断条件是包含：/2018-09-03/doc-类似的结构
     *
     * @param url
     * @return
     */
    private boolean isFinalNewsPage(String url) {
        //注意：默认只要今天的新闻(可以设置“今天”的时间)
        String date = StringUtils.isEmpty(exactDate) ? dateFormat.format(new Date()) : exactDate;
        if (!url.contains(date)) {
            return false;
        }
        boolean beginScan = false;
        int n = 0;
        for (char c : url.toCharArray()) {
            if (beginScan) {  //开始扫描验证 '/' 后面的结构
                if (n == 16) {
                    return true;
                }
                if ((((n >= 1 && n <= 4) || (n >= 6 && n <= 7) || (n >= 9 && n <= 10)) && !Character.isDigit(c))
                        || ((n == 5 || n == 8 || n == 15) && c != '-')
                        || (n == 11 && c != '/')
                        || (n == 12 && c != 'd')
                        || (n == 13 && c != 'o')
                        || (n == 14 && c != 'c')) {  //不符合 / 后面的模式
                    beginScan = false;
                    n = 0;
                } else {
                    n++;
                }
            } else if (c == '/') {
                beginScan = true;
                n++;
            }
        }
        return false;
    }

    /**
     * 提取出该页面的站内链接
     *
     * @param url
     * @return
     */
    private List<String> extractSinaUrl(String url) {
        String content = HttpUtil.getRequest(url, "iso-8859-1");
        if (StringUtils.isEmpty(content)) {
            return Collections.emptyList();
        }
        List<String> links = new ArrayList<>();
        int start = content.indexOf("href=\"");
        while (start != -1) {
            int end = content.indexOf("\"", start + 6);
            if (end != -1) {
                String link = content.substring(start + 6, end).trim();
                if ((link.startsWith("http://news.sina.com.cn") || link.startsWith("https://news.sina.com.cn"))
                        && !visitedUrlSet.contains(link) && !isOldNewsLink(link)) {  //过滤掉年代久远的新闻，避免因为这些链接导致宽度遍历太发散
                    links.add(link.trim());
                }
            }
            start = content.indexOf("href=\"", start + 6);
        }
        return links.size() > 0 ? links : Collections.emptyList();
    }

    /**
     * 判断一个链接是否是非当天的新闻链接，如果是则过滤，因为过去的新闻不可能链接到今天的新闻
     *
     * @param link
     * @return
     */
    private boolean isOldNewsLink(String link) {
        if (StringUtils.isEmpty(link)) {
            return false;
        }
        //先判断是不是新闻页面
        int n = 0;
        boolean beginScan = false, isNewsPage = false;
        String thisDate = null;
        for (char c : link.toCharArray()) {
            if (beginScan) {
                if (n == 16) {
                    isNewsPage = true;
                    int docIndex = link.lastIndexOf("/doc-");
                    thisDate = link.substring(link.lastIndexOf("/", docIndex - 1) + 1, docIndex);
                    break;
                } else {
                    if ((((n >= 1 && n <= 4) || (n >= 6 && n <= 7) || (n >= 9 && n <= 10)) && !Character.isDigit(c))
                            || ((n == 5 || n == 8 || n == 15) && c != '-')
                            || (n == 11 && c != '/')
                            || (n == 12 && c != 'd')
                            || (n == 13 && c != 'o')
                            || (n == 14 && c != 'c')) {
                        beginScan = false;
                        n = 0;
                    } else {
                        n++;
                    }
                }
            } else if (c == '/') {
                beginScan = true;
                n = 1;
            }
        }
        if (!isNewsPage) {
            return false;
        }
        String date = StringUtils.isEmpty(exactDate) ? dateFormat.format(new Date()) : exactDate;
        DateTime shouldDate = new DateTime(date), actualDate = new DateTime(thisDate);
        if (actualDate.isBefore(shouldDate)) {
            return true;
        }
        return false;
    }

    @Override
    public void setDate(String date) {
        exactDate = date;
    }

}
