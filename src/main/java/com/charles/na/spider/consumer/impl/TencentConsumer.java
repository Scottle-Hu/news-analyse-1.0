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
 * 腾讯新闻的爬虫消费者
 *
 * @author huqj
 */
@Service("tencentConsumer")
@Log4j
public class TencentConsumer extends ConsumerSpider {

    @Autowired
    private NewsMapper newsMapper;

    /**
     * 用zk同步已经消费的数量
     */
    @Autowired
    private PushedArticleNumZK pushedArticleNumZk;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    SimpleDateFormat publishTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String dateStr = dateFormat.format(new Date());//爬虫解析不出日期时的默认日期

    @Override
    public void accept(byte[] bytes) {
        try {
            //从消费者获取的byte流构造字符串
            String link = new String(bytes, "utf-8");
            log.info("[tencent] start build news from " + link);
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
            log.error("[tencent] error when consume url.", e);
        }
    }

    /**
     * 从腾讯新闻url分析页面，构造新闻对象
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
            news.setId("ifeng_" + url.substring(url.lastIndexOf("/") + 1, url.length()));

            ///////////////////////// set title ////////////////////////
            int titleStart = content.indexOf("<h1>");
            if (titleStart == -1) {
                news.setTitle("");  //标题缺失
            } else {
                int titleEnd = content.indexOf("</h1>", titleStart);
                if (titleEnd == -1) {
                    news.setTitle("");
                } else {
                    news.setTitle(content.substring(titleStart + 4, titleEnd));
                }
            }

            /////////////////////// set time //////////////////////////
            int timeStart = content.indexOf("name=\"apub:time\"");
            if (timeStart == -1) {
                news.setTime(dateStr);
            } else {
                timeStart = content.indexOf("content=\"", timeStart);
                if (timeStart == -1) {
                    news.setTime(dateStr);
                } else {
                    int timeEnd = content.indexOf("\"", timeStart + 9);
                    if (timeEnd == -1) {
                        news.setTime(dateStr);
                    } else {
                        String timeWholeStr = content.substring(timeStart + 9, timeEnd).trim();
                        try {
                            news.setTime(dateFormat.format(publishTimeFormat.parse(timeWholeStr)));
                        } catch (ParseException e) {
                            log.error("[tencent] error parse publish time.", e);
                            news.setTime(dateStr);
                        }
                    }
                }
            }

            //////////////////// set source ///////////////////////
            int sourceStart = content.indexOf("\"media\": \"");
            if (sourceStart == -1) {
                news.setSource("来源缺失");
            } else {
                int sourceEnd = content.indexOf("\"", sourceStart + 10);
                if (sourceEnd == -1) {
                    news.setSource("来源缺失");
                } else {
                    news.setSource(content.substring(sourceStart + 10, sourceEnd).trim());
                }
            }

            //////////////////// set  content ////////////////////
            //考虑到内容比较重要，若无法解析出正文则直接返回null
            int articleStart = content.indexOf("<div class=\"content-article\">");
            if (articleStart == -1) {
                return null;
            } else {
                int articleEnd = content.indexOf("<div id=\"Status\">", articleStart);
                if (articleEnd == -1) {
                    return null;
                } else {
                    String con = cleanHTMLTag(content.substring(articleStart + 29, articleEnd));
                    if (con == null) {
                        return null;
                    }
                    news.setContent(con);
                }
            }

            //////////////////// set keywords ////////////////////
            int keywordStart = content.indexOf("\"tags\": \"");
            if (keywordStart == -1) {
                news.setKeywords("");
            } else {
                int keywordEnd = content.indexOf("\"", keywordStart + 9);
                if (keywordEnd == -1) {
                    news.setKeywords("");
                } else {
                    String keys = content.substring(keywordStart + 9, keywordEnd);
                    keys = keys.replace(";", ",")
                            .replace(",腾讯网,腾讯新闻", "");
                    news.setKeywords(keys);
                }
            }

            /////////////////// set visitNum ///////////////////
            //TODO 暂时先设置为0
            news.setVisitNum(0);
            news.setRemarkNum(0);

            ////////////////// set catch time //////////////////
            news.setCatchTime(System.currentTimeMillis());

            return news;
        } catch (Exception e) {
            log.error("[tencent] error when build news from html src.", e);
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
