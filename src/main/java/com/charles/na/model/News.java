package com.charles.na.model;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 新闻实体类
 * @since 1.0
 */
public class News {

    /**
     * id
     */
    private String id;
    /**
     * 新闻标题
     */
    private String title;
    /**
     * 新闻发布时间
     */
    private String time;
    /**
     * 新闻来源
     */
    private String source;
    /**
     * 新闻内容
     */
    private String content;
    /**
     * 关键词
     */
    private String keywords;
    /**
     * 浏览量
     */
    private int visitNum;
    /**
     * 评论数量
     */
    private int remarkNum;
    /**
     * 网址url
     */
    private String url;
    /**
     * 抓取时间戳
     */
    private long catchTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(int visitNum) {
        this.visitNum = visitNum;
    }

    public int getRemarkNum() {
        return remarkNum;
    }

    public void setRemarkNum(int remarkNum) {
        this.remarkNum = remarkNum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getCatchTime() {
        return catchTime;
    }

    public void setCatchTime(long catchTime) {
        this.catchTime = catchTime;
    }

    @Override
    public String toString() {
        return "News{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", source='" + source + '\'' +
                ", content='" + content + '\'' +
                ", keywords='" + keywords + '\'' +
                ", visitNum=" + visitNum +
                ", remarkNum=" + remarkNum +
                ", url='" + url + '\'' +
                ", catchTime=" + catchTime +
                '}';
    }
}
