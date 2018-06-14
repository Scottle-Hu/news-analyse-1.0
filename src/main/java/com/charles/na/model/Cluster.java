package com.charles.na.model;

/**
 * 聚类和新闻的关系
 *
 * @author huqj
 */
public class Cluster {

    private String id;

    private String newsId;

    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
