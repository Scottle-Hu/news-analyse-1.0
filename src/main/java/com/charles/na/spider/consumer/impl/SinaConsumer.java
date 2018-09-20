package com.charles.na.spider.consumer.impl;

import com.charles.na.mapper.NewsMapper;
import com.charles.na.model.News;
import com.charles.na.spider.consumer.ConsumerSpider;
import com.charles.na.spider.service.PushedArticleNumZK;
import com.charles.na.utils.HttpUtil;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 实现新浪页面的抓取和解析
 *
 * @author Charles
 */
@Log4j
@Service("sinaConsumer")
public class SinaConsumer extends ConsumerSpider {

    private Logger LOGGER = Logger.getLogger(SinaConsumer.class);

    @Autowired
    private NewsMapper newsMapper;

    /**
     * 用zk同步已经消费的数量
     */
    @Autowired
    private PushedArticleNumZK pushedArticleNumZk;

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
            LOGGER.info("start build news from " + link);
            //构造新闻
            News news = buildNewsFromUrl(link);
            if (news == null) {
                return;
            }
            //插入新闻
            newsMapper.insert(news);
            pushedArticleNumZk.add();
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("error when consume url.", e);
        }
    }

    /**
     * 从url分析页面，构造新闻对象
     *
     * @param url
     * @return
     */
    private News buildNewsFromUrl(String url) {
        String content = HttpUtil.getRequest(url);
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        try {
            News news = new News();
            news.setUrl(url);
            //用url后半段做id保证不重复
            news.setId(url.substring(url.lastIndexOf("/") + 1, url.length()));

            ///////////////////////// set title ////////////////////////
            int titleStart = content.indexOf("class=\"main-title\">");
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
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());//默认
            int timeStart = content.indexOf("class=\"date\">");
            if (timeStart == -1) {
                news.setTitle(dateStr);
            } else {
                int timeEnd = content.indexOf("</span>", timeStart);
                if (timeEnd == -1) {
                    news.setTime(dateStr);
                } else {
                    String timeWholeStr = content.substring(timeStart + 13, timeEnd);
                    if (timeWholeStr.contains("日")) {
                        timeWholeStr = timeWholeStr.substring(0, timeWholeStr.indexOf("日"));
                    }
                    timeWholeStr = timeWholeStr.replace("年", "-")
                            .replace("月", "-");
                    news.setTime(timeWholeStr);
                }
            }

            //////////////////// set source ///////////////////////
            int sourceStart = content.indexOf("class=\"source\"");
            if (sourceStart == -1) {
                news.setSource("来源缺失");
            } else {
                sourceStart = content.indexOf(">", sourceStart);
                if (sourceStart == -1) {
                    news.setSource("来源缺失");
                } else {
                    int sourceEnd = content.indexOf("</", sourceStart);
                    if (sourceEnd == -1) {
                        news.setSource("来源缺失");
                    } else {
                        news.setSource(content.substring(sourceStart + 1, sourceEnd));
                    }
                }
            }

            //////////////////// set  content ////////////////////
            //考虑到内容比较重要，若无法解析出正文则直接返回null
            int articleStart = content.indexOf("class=\"article\"");
            if (articleStart == -1) {
                return null;
            } else {
                articleStart = content.indexOf(">", articleStart);
                if (articleStart == -1) {
                    return null;
                } else {
                    int articleEnd = content.indexOf("<p class=\"show_author\">", articleStart);
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
            int keywordStart = content.indexOf("class=\"keywords\"");
            if (keywordStart == -1) {
                news.setKeywords("");
            } else {
                keywordStart = content.indexOf("data-wbkey=\"");
                if (keywordStart == -1) {
                    news.setKeywords("");
                } else {
                    keywordStart += 12;
                    int keywordEnd = content.indexOf("\"", keywordStart);
                    if (keywordEnd == -1) {
                        news.setKeywords("");
                    } else {
                        news.setKeywords(content.substring(keywordStart, keywordEnd));
                    }
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
            LOGGER.error("error when build news from html src.", e);
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
