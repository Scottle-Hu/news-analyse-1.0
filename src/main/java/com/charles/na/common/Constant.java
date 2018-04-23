package com.charles.na.common;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 常量
 * @since 1.0
 */
//scottle-hu  hqj19971225
public class Constant {

    public static int TITLE_WEIGHT = 10;   //单词袋子模型中标题词语所占权重

    public static int TRY_MAX = 10;       //数据库连接失败最多重新尝试的次数

    public static int WAIT_MILLIS = 10000; //数据库连接失败后重连的等待毫秒

    public static int MAX_THREAD_NUM = 8; //线程池最大个数
}
