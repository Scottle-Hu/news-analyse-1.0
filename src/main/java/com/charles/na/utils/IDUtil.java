package com.charles.na.utils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Charles
 * @create 2018/3/28
 * @description id生成工具类
 * @since 1.0
 */
public class IDUtil {
    private static Queue<String> lastestStrIds = new LinkedList<String>();
    private static Queue<Long> lastestLongIds = new LinkedList<Long>();

    public static String generateID() {
        String res = generateID2();
        while (lastestStrIds.contains(res)) {
            res = generateID2();
        }
        if (lastestStrIds.size() > 1000) {
            lastestStrIds.poll();
        }
        lastestStrIds.offer(res);
        return res;
    }

    private static String generateID2() {
        String id = "";
        long times = System.currentTimeMillis();
        int r = (int) (Math.random() * 10000);
        id = id + times + r;
        return id;
    }

    public static long generateLongID() {
        long res = generateLongID2();
        while (lastestLongIds.contains(res)) {
            res = generateLongID2();
        }
        if (lastestLongIds.size() > 1000) {
            lastestLongIds.poll();
        }
        lastestLongIds.offer(res);
        return res;
    }

    private static long generateLongID2() {
        long id = 0L;
        long times = System.currentTimeMillis();
        int r = (int) (Math.random() * 1000);
        id = times * 1000 + r;
        return id;
    }
}
