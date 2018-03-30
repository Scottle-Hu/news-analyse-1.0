package com.charles.na.utils;

/**
 * @author Charles
 * @create 2018/3/30
 * @description api接口校验工具
 * @since 1.0
 */
public class SignUtil {

    public static String generateSign(String token) {
        return token.hashCode() + "";
    }

}
