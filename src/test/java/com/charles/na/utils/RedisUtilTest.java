package com.charles.na.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class RedisUtilTest {

    @Test
    public void testJedis() {
        Jedis jedis = new Jedis("my", 6379);
        jedis.auth("charles!!!1997");
    }

    @Test
    public void testRedisCon() {
        RedisUtil redisUtil = new RedisUtil("my", 6379, "charles!!!1997");
        String res = redisUtil.getInstance().ping();
        System.out.println(res);
        assertEquals(res, "PONG");
    }

    @Test
    public void testSet() {
        RedisUtil redisUtil = new RedisUtil("my", 6379, "charles!!!1997");
        redisUtil.set("test_from_remote", "123");
    }

    @Test
    public void testExpire() throws InterruptedException {
        String key = "test_from_remote";
        String value = "test";
        RedisUtil redisUtil = new RedisUtil("my", 6379, "charles!!!1997");
        redisUtil.set(key, value);
        redisUtil.setExpire(key, 5);
        String v = redisUtil.get(key);
        System.out.println("过期之前：" + v);
        assert v.equals(value);
        Thread.sleep(5000);
        v = redisUtil.get(key);
        System.out.println("过期之后：" + v);
        assert v == null;
    }

}