package com.charles.na.model;

/**
 * @author huqj
 * @create 2018/4/17
 * @description entity for "t_topic"
 */
public class Topic {

    private String id;

    /**
     * 话题名称
     */
    private String topic;

    /**
     * 话题的创建日期
     */
    private String date;

    /**
     * 话题的热度
     */
    private int hot;

    /**
     * 此话题的新闻数量
     */
    private int newsNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public int getNewsNum() {
        return newsNum;
    }

    public void setNewsNum(int newsNum) {
        this.newsNum = newsNum;
    }
}
