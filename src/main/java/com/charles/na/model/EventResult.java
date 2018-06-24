package com.charles.na.model;

import com.charles.na.web.model.NameAndValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;

/**
 * 存储在mongo中的结果字段
 *
 * @author huqj
 */
public class EventResult {

    private String viewPoint;

    private String id;

    private String title;

    private int curHot;

    private String hot;

    private Map<String, List> hotTend = new HashMap<String, List>();

    private List<NameAndValue> sentimentMap;

    private List<NameAndValue> peopleMap;

    private String sentiment;

    private String people;

    private List<News> newsList;

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

    public int getCurHot() {
        return curHot;
    }

    public void setCurHot(int curHot) {
        this.curHot = curHot;
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

    public List<News> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    public Map<String, List> getHotTend() {
        return hotTend;
    }

    public void setHotTend(Map<String, List> hotTend) {
        this.hotTend = hotTend;
    }

    public List<NameAndValue> getSentimentMap() {
        return sentimentMap;
    }

    public void setSentimentMap(List<NameAndValue> sentimentMap) {
        this.sentimentMap = sentimentMap;
    }

    public List<NameAndValue> getPeopleMap() {
        return peopleMap;
    }

    public void setPeopleMap(List<NameAndValue> peopleMap) {
        this.peopleMap = peopleMap;
    }

    public String getViewPoint() {
        return viewPoint;
    }

    public void setViewPoint(String viewPoint) {
        this.viewPoint = viewPoint;
    }

    @Override
    public String toString() {
        return "EventResult{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", curHot=" + curHot +
                ", hot='" + hot + '\'' +
                ", sentiment='" + sentiment + '\'' +
                ", people='" + people + '\'' +
                ", newsList=" + newsList +
                '}';
    }
}
