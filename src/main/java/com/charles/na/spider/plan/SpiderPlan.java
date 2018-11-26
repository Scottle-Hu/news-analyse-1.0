package com.charles.na.spider.plan;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 一个爬虫计划，在service中可以使用多线程一次运行多个爬虫计划，
 * 每个爬虫计划包括爬虫的生产者，消费者
 *
 * @author Charles
 */
public abstract class SpiderPlan extends Thread {

    protected String planName;

    /**
     * 当有多个线程的时候，用于线程通知的对象
     */
    private List<Integer> threads;

    /**
     * 创建子线程开始一个爬虫计划
     */
    @Override
    public void run() {
        if (threads == null) {
            return;
        }
        beginSpider();
        //此任务结束，唤醒主线程
        synchronized (threads) {
            threads.remove(0);
        }
        waitOrNotify(threads);
    }

    public abstract void beginSpider();

    public static void waitOrNotify(List<Integer> threads) {
        synchronized (threads) {
            if (threads.size() == 0) {
                threads.notifyAll();
            } else {
                try {
                    threads.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Integer> getThreads() {
        return threads;
    }

    public void setThreads(List<Integer> threads) {
        this.threads = threads;
    }

    public abstract void setDate(String date);

    public abstract void setPlanName(String planName);

    public abstract String getPlanName();
}
