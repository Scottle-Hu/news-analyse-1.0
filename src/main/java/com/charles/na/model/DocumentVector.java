package com.charles.na.model;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 预处理后的文本向量类
 * @since 1.0
 */
public class DocumentVector {
    /**
     * id
     */
    private String id;
    /**
     * 对应的新闻的id
     */
    private String newsId;
    /**
     * 权重向量：(word1:weight1 word2:weight2 ......)
     */
    private String vector;
    /**
     * 计算时间
     */
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

    public String getVector() {
        return vector;
    }

    public void setVector(String vector) {
        this.vector = vector;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DocumentVector{" +
                "id='" + id + '\'' +
                ", newsId='" + newsId + '\'' +
                ", vector='" + vector + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((DocumentVector) obj).getId());
    }
}
