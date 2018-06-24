package com.charles.na.web.model;

/**
 * 用户与事件的订阅关系
 *
 * @author huqj
 */
public class Subscribe {

    private String userId;

    private String eventId;

    private String date;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
