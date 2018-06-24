package com.charles.na.utils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.commonj.TimerManagerTaskScheduler;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * redis操作的封装
 *
 * @author huqj
 */
public class RedisUtil {

    private Logger LOGGER = Logger.getLogger(RedisUtil.class);

    private String redisHost;

    private int port;

    private Jedis jedis;

    private String pass;

    private Map<String, String> cache = new HashMap<String, String>();

    private Map<String, Long> expireMap = new HashMap<String, Long>();

    @PostConstruct
    private void init() {  //定时清理过期key
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (Map.Entry<String, Long> e : expireMap.entrySet()) {
                    if (now > e.getValue() && cache.get(e.getKey()) != null) {
                        cache.remove(e.getKey());
                    }
                }
                System.out.println("当前登录缓存：");
                for (Map.Entry<String, String> e : cache.entrySet()) {
                    System.out.println(e.getKey() + ":" + e.getValue());
                }
            }
        }, 0, 60000);
    }

    public RedisUtil(String redisHost, int port, String pass) {
        this.redisHost = redisHost;
        this.port = port;
        this.pass = pass;
    }

    public Jedis getInstance() {
        if (jedis == null || !jedis.isConnected()) {
            jedis = new Jedis(redisHost, port);
            jedis.auth(pass);
        }
        return jedis;
    }

    public void set(String key, String value) {
        getInstance().set(key, value);
    }

    public String get(String key) {
        return getInstance().get(key);
    }

    public void setExpire(String key, int second) {
        getInstance().expire(key, second);
    }

    public void del(String key) {
        getInstance().del(key);
    }

    //测试的时候在内存中存储redis缓存的对应方法
    public void set2(String key, String value) {
        cache.put(key, value);
    }

    public String get2(String key) {
        return cache.get(key);
    }

    public void setExpire2(String key, int second) {
        expireMap.put(key, System.currentTimeMillis() + (long) (second * 1000));
    }

    public void del2(String key) {
        cache.remove(key);
    }


}
