package com.charles.na.spider.service;

import lombok.extern.log4j.Log4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * 用于在生产者和消费者之间同步新闻消费数量的zk
 *
 * @author huqj
 */
@Service("pushedArticleNumZk")
@Log4j
public class PushedArticleNumZK {

    /**
     * 记录已经消费的新闻数量
     */
    private int pushArticleNum;

    /**
     * zk上记录已经消费的新闻数量的路径，用于生产者和消费者之间的通信
     */
    @Value("${zookeeper.spider.completed}")
    private String zkCompletedArticleNumPath;

    @Value("${zookeeper.timeout}")
    private int timeout;

    /**
     * 监听已经消费文章数量的zk
     */
    private ZooKeeper zk;

    @PostConstruct
    public void init() {
        log.info("init zkCompletedArticleNumPath=" + zkCompletedArticleNumPath
                + ", timeout=" + timeout);
        initPushedArticleNum();
    }

    /**
     * 消费+1
     */
    public synchronized void add() {
        try {
            zk.setData(zkCompletedArticleNumPath, String.valueOf(pushArticleNum + 1).getBytes(),
                    zk.exists(zkCompletedArticleNumPath, false).getVersion());
        } catch (Exception e) {
            log.error("error when add pushedArticleNum to zk.", e);
        }
    }

    /**
     * 获取已经消费的数量
     *
     * @return
     */
    public synchronized int get() {
        return pushArticleNum;
    }

    /**
     * 监听文章消费数量变化的watcher
     */
    private Watcher articleNumWatcher = new Watcher() {
        @Override
        public synchronized void process(WatchedEvent watchedEvent) {
            if (watchedEvent.getType().equals(Event.EventType.NodeDataChanged)) {
                try {
                    pushArticleNum = Integer.parseInt(
                            new String(zk.getData(zkCompletedArticleNumPath, articleNumWatcher, null)));
                } catch (Exception e) {
                    log.error("error when update pushArticleNum.", e);
                }
            }
        }
    };

    /**
     * 初始化已消费文章数量和监听数量变化
     */
    private void initPushedArticleNum() {
        try {
            zk = new ZooKeeper(zkCompletedArticleNumPath, timeout, articleNumWatcher);
        } catch (IOException e) {
            log.error("error when create zk.", e);
            return;
        }
        try {
            //初始化置消费数量为0
            zk.setData(zkCompletedArticleNumPath, "0".getBytes(),
                    zk.exists(zkCompletedArticleNumPath, false).getVersion());
            //设置监听
            zk.getData(zkCompletedArticleNumPath, articleNumWatcher, null);
            pushArticleNum = 0;
        } catch (Exception e) {
            log.error("error when set zk listener.", e);
            return;
        }
        log.info("init pushedArticleNumZk successfully.");
    }

}
