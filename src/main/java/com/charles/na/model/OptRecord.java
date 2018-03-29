package com.charles.na.model;

/**
 * @author Charles
 * @create 2018/3/29
 * @description 操作记录类
 * @since 1.0
 */
public class OptRecord {

    private String id;

    private String opt;

    private String time;

    private int optNum;

    private String costTime;

    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getOptNum() {
        return optNum;
    }

    public void setOptNum(int optNum) {
        this.optNum = optNum;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
