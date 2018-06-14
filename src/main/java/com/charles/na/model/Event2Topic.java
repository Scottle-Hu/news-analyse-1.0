package com.charles.na.model;

/**
 * 事件与话题的关联类
 *
 * @author huqj
 */
public class Event2Topic {

    private String eventId;

    private String topicId;

    private String date;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
