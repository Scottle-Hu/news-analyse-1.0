package com.charles.na.web.model;

import com.charles.na.model.EventResult;

import java.util.*;

/**
 * 分析系统首页的信息封装
 *
 * @author huqj
 */
public class MainPageInfo {

    /**
     * 查询的起始时间
     */
    private String startDate;

    /**
     * 查询的结束时间
     */
    private String endDate;

    /**
     * 热点关键词
     */
    private List<NameAndValue> hotWords = new ArrayList<NameAndValue>();

    /**
     * 热点人物
     */
    private List<NameAndValue> hotPeople = new ArrayList<NameAndValue>();

    /**
     * 负面预警
     */
    private List<NameAndValue> negativeEvent = new ArrayList<NameAndValue>();

    /**
     * 热点话题排行
     */
    private List<EventResult> topEvent;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<NameAndValue> getHotWords() {
        return hotWords;
    }

    public void setHotWords(List<NameAndValue> hotWords) {
        this.hotWords = hotWords;
    }

    public List<NameAndValue> getHotPeople() {
        return hotPeople;
    }

    public void setHotPeople(List<NameAndValue> hotPeople) {
        this.hotPeople = hotPeople;
    }

    public List<NameAndValue> getNegativeEvent() {
        return negativeEvent;
    }

    public void setNegativeEvent(List<NameAndValue> negativeEvent) {
        this.negativeEvent = negativeEvent;
    }

    public List<EventResult> getTopEvent() {
        return topEvent;
    }

    public void setTopEvent(List<EventResult> topEvent) {
        this.topEvent = topEvent;
    }

    @Override
    public String toString() {
        return "MainPageInfo{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", hotWords=" + hotWords +
                ", hotPeople=" + hotPeople +
                ", negativeEvent=" + negativeEvent +
                ", topEvent=" + topEvent +
                '}';
    }
}
