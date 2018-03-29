package com.charles.na.utils;

/**
 * @author Charles
 * @create 2018/3/28
 * @description id生成工具类
 * @since 1.0
 */
public class IDUtil {

    public static String generateID() {
        String id = "";
        long times = System.currentTimeMillis();
        int r = (int) Math.random() * 10000;
        id = id + times + r;
        return id;
    }
}
