package com.charles.na.utils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;

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


}
