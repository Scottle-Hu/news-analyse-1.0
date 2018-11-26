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
 * 网易新闻的爬虫生产者
 *
 * @author huqj
 */
@Service("neteaseProducer")
@Log4j
public class NeteaseProducer implements ProducerSpider {

    private String rootUrl = "https://news.163.com/";

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

    /**
     * 网易新闻从链接判断日期的格式是: https://news.163.com/18/1126/14/E1HVT1H40001875N.html
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("/yy/MMdd/");

    private SimpleDateFormat dateFormatNormal = new SimpleDateFormat("yyyy-MM-dd");

    @PostConstruct
    public void init() {
        //定时同步url列表长度，用来作为调整队列长度限制的依据
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("[netease] toVisitUrlList length: " + toVisitUrlList.size()
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
                log.error("[netease] error when produce url from: " + url, e);
            }
        }
        //队列空了导致没有新闻继续抓取，打个warning
        if (toVisitUrlList.isEmpty()) {
            log.warn("[netease] toVisitUrlList is empty! Producing stopped!");
        }
    }

    /**
     * 判断该链接是否是网易新闻当日的最新链接
     * 示例： https://news.163.com/18/1126/14/E1HVT1H40001875N.html
     *
     * @param url
     * @return
     */
    private boolean isFinalNewsPage(@NotNull String url) {
        String dateStr = StringUtils.isEmpty(exactDate) ? dateFormat.format(new Date()) : exactDate;
        if (!url.contains("news.163.com" + dateStr) || !url.endsWith(".html")) {
            return false;
        }
        int index = url.indexOf(dateStr) + 9, i = index;
        while (i < index + 3) {
            if (i < index + 2 && !Character.isDigit(url.charAt(i))) {
                return false;
            }
            if (i == index + 2 && url.charAt(i) != '/') {
                return false;
            }
            i++;
        }
        if (url.indexOf("/", i) != -1) {
            return false;
        }
        return true;
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
                if ((link.startsWith("http://news.163.com") || link.startsWith("https://news.163.com"))
                        && !visitedUrlSet.contains(link) && !isOldNewsLink(link)) {
                    links.add(link.trim());
                }
            }
            start = content.indexOf("href=\"", start + 6);
        }
        return links.size() > 0 ? links : Collections.emptyList();
    }

    /**
     * 判断一个链接是否是老新闻页面链接
     * 示例： https://news.163.com/18/1126/14/E1HVT1H40001875N.html
     *
     * @param link
     * @return
     */
    private boolean isOldNewsLink(String link) {
        if (StringUtils.isEmpty(link)) {
            return false;
        }
        int index = link.indexOf("news.163.com/");
        if (index != -1) {
            index += 13;
        } else {
            return false;
        }
        char[] linkChars = link.toCharArray();
        if (linkChars.length < index + 11) {
            return false;
        }
        if (Character.isDigit(linkChars[index]) && Character.isDigit(linkChars[index + 1])
                && linkChars[index + 2] == '/' && Character.isDigit(linkChars[index + 3])
                && Character.isDigit(linkChars[index + 4]) && Character.isDigit(linkChars[index + 5])
                && Character.isDigit(linkChars[index + 6]) && linkChars[index + 7] == '/'
                && Character.isDigit(linkChars[index + 8]) && Character.isDigit(linkChars[index + 9])
                && linkChars[index + 10] == '/') {
            String thisDateStr = link.substring(index - 1, index + 8);
            try {
                Date thisDate = dateFormat.parse(thisDateStr);
                Date today = dateFormat
                        .parse(StringUtils.isEmpty(exactDate) ? dateFormat.format(new Date()) : exactDate);
                if (thisDate.before(today)) {
                    return true;
                }
            } catch (ParseException e) {
                log.error("[netease] error parse date string.", e);
            }
        }
        return false;
    }

    @Override
    public void setDate(String date) {
        try {
            //日期从yyyy-MM-dd转换成/yy/MMdd/
            this.exactDate = dateFormat.format(dateFormatNormal.parse(date));
        } catch (ParseException e) {
            log.error("[netease] error parse exact date.", e);
        }
    }

}
