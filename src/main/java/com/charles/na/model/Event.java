package com.charles.na.model;

/**
 * 事件实体，事件是由具有时间先后顺序的话题组成的话题序列，
 * 同时包括热度趋势，情感倾向，事件涉及的热点人物等信息
 *
 * @author huqj
 */
public class Event {

    private String id;

    private String title;

    private String hot;

    private String sentiment;

    private String people;

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

    public String getHot() {
        return hot;
    }

    public void setHot(String hot) {
        this.hot = hot;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", hot='" + hot + '\'' +
                ", sentiment='" + sentiment + '\'' +
                ", people='" + people + '\'' +
                '}';
    }
}
