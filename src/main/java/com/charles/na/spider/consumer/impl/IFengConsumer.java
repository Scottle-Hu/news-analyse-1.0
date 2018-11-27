package com.charles.na.spider.consumer.impl;

import com.charles.na.mapper.NewsMapper;
import com.charles.na.model.News;
import com.charles.na.spider.consumer.ConsumerSpider;
import com.charles.na.spider.service.PushedArticleNumZK;
import com.charles.na.utils.HttpUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 凤凰新闻的爬虫消费者
 *
 * @author huqj
 */
@Service("iFengConsumer")
@Log4j
public class IFengConsumer extends ConsumerSpider {

    @Autowired
    private NewsMapper newsMapper;

    /**
     * 用zk同步已经消费的数量
     */
    @Autowired
    private PushedArticleNumZK pushedArticleNumZk;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    SimpleDateFormat publishTimeFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    String dateStr = dateFormat.format(new Date());//爬虫解析不出日期时的默认日期

    @Override
    public void accept(byte[] bytes) {
        try {
            //从消费者获取的byte流构造字符串
            String link = new String(bytes, "utf-8");
            log.info("[ifeng] start build news from " + link);
            //构造新闻
            News news = buildNewsFromUrl(link);
            System.out.println("===============================================\n" + news + "\n");
            if (news == null) {
                return;
            }
            //插入新闻
            newsMapper.insert(news);
            pushedArticleNumZk.add();
        } catch (UnsupportedEncodingException e) {
            log.error("[ifeng] error when consume url.", e);
        }
    }

    /**
     * 从凤凰新闻url分析页面，构造新闻对象
     *
     * @param url
     * @return
     */
    private News buildNewsFromUrl(String url) {
        String content = HttpUtil.getRequest(url, null);
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        try {
            News news = new News();
            news.setUrl(url);
            //用url后半段做id保证不重复
            news.setId("ifeng_" + url.substring(url.indexOf("/a/") + 3, url.length()));

            ///////////////////////// set title ////////////////////////
            int titleStart = content.indexOf("id=\"artical_topic\">");
            if (titleStart == -1) {
                news.setTitle("");  //标题缺失
            } else {
                int titleEnd = content.indexOf("</h1>", titleStart);
                if (titleEnd == -1) {
                    news.setTitle("");
                } else {
                    news.setTitle(content.substring(titleStart + 19, titleEnd));
                }
            }

            /////////////////////// set time //////////////////////////
            int timeStart = content.indexOf("class=\"ss01\">");
            if (timeStart == -1) {
                news.setTime(dateStr);
            } else {
                int timeEnd = content.indexOf("</span>", timeStart);
                if (timeEnd == -1) {
                    news.setTime(dateStr);
                } else {
                    String timeWholeStr = content.substring(timeStart + 13, timeEnd).trim();
                    try {
                        news.setTime(dateFormat.format(publishTimeFormat.parse(timeWholeStr)));
                    } catch (ParseException e) {
                        log.error("[ifeng] error parse publish time.", e);
                        news.setTime(dateStr);
                    }
                }
            }

            //////////////////// set source ///////////////////////
            int sourceStart = content.indexOf("ref=\"nofollow\">");
            if (sourceStart == -1) {
                news.setSource("来源缺失");
            } else {
                int sourceEnd = content.indexOf("</", sourceStart);
                if (sourceEnd == -1) {
                    news.setSource("来源缺失");
                } else {
                    news.setSource(content.substring(sourceStart + 15, sourceEnd).trim());
                }
            }

            //////////////////// set  content ////////////////////
            //考虑到内容比较重要，若无法解析出正文则直接返回null
            int articleStart = content.indexOf("id=\"main_content\"");
            if (articleStart == -1) {
                return null;
            } else {
                articleStart = content.indexOf(">", articleStart);
                if (articleStart == -1) {
                    return null;
                } else {
                    int articleEnd = content.indexOf("<div class=\"relateNews", articleStart);
                    if (articleEnd == -1) {
                        return null;
                    } else {
                        String con = cleanHTMLTag(content.substring(articleStart + 1, articleEnd));
                        if (con == null) {
                            return null;
                        }
                        news.setContent(con);
                    }
                }
            }

            //////////////////// set keywords ////////////////////
            news.setKeywords("");  //凤凰新闻没有关键词

            /////////////////// set visitNum ///////////////////
            //TODO 暂时先设置为0
            news.setVisitNum(0);
            news.setRemarkNum(0);

            ////////////////// set catch time //////////////////
            news.setCatchTime(System.currentTimeMillis());

            return news;
        } catch (Exception e) {
            log.error("[ifeng] error when build news from html src.", e);
        }
        return null;
    }

    /**
     * 去除正文中的html标签
     *
     * @param src
     * @return
     */
    private String cleanHTMLTag(String src) {
        if (src == null) {
            return null;
        }
        int cleanStart = src.indexOf("<");
        while (cleanStart != -1) {
            int cleanEnd = src.indexOf(">", cleanStart);
            if (cleanEnd == -1) {
                break;
            }
            String tag = src.substring(cleanStart, cleanEnd + 1);
            src = src.replace(tag, "");
            cleanStart = src.indexOf("<");
        }
        return src.trim();
    }

}
