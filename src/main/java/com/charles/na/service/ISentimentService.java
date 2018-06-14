package com.charles.na.service;

/**
 * 对新闻内容进行情感倾向分析的接口类
 *
 * @author huqj
 */
public interface ISentimentService {

    /**
     * 分析情感倾向，以json形式返回
     *
     * @param content
     * @return
     */
    String parseSentiment(String content);

}
