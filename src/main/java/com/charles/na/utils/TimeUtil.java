package com.charles.na.utils;

/**
 * @author Charles
 * @create 2018/3/28
 * @since 1.0
 */
public class TimeUtil {

    public static String convertMillis2String(long millis) {
        if (millis < 1000) {
            return millis + "毫秒";
        } else if (millis < 1000 * 60) {
            return millis / 1000 + "秒" + millis % 1000 + "毫秒";
        } else if (millis < 1000 * 60 * 60) {
            return millis / (1000 * 60) + "分钟"
                    + (millis % (1000 * 60)) / 1000 + "秒"
                    + (millis % (1000 * 60)) % 1000 + "毫秒";
        } else {
            return millis / (1000 * 60 * 60) + "小时"
                    + (millis % (1000 * 60 * 60)) / (1000 * 60) + "分钟"
                    + ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000 + "秒"
                    + ((millis % (1000 * 60 * 60)) % (1000 * 60)) % 1000 + "毫秒";
        }
    }
}
