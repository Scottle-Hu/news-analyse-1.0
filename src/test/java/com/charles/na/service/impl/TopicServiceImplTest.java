package com.charles.na.service.impl;

import com.charles.na.service.ITopicService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class TopicServiceImplTest {

    private String text = "每年的I/O大会是谷歌公司的重头戏，大会上不仅会有谷歌的新技术与新产品，同时也吸引众多开发者们热情参与。据国外媒体报道，谷歌今年将举办的I/O大会时间已经确定，大会将于美国西部时间5月28日至29日正式举行，举办地点仍为旧金山莫斯考尼展览馆。　　据悉，本次活动将于3月17日至19日开启注册，不过目前尚且没有关于此次大会门票价格的信息。2014年谷歌I/O大会双日门票价格为900美元，教师身份则可以享受300美元的优惠价格。　　按照惯例，谷歌在I/O大会举办的同时会推出一款相应的手机应用，不能到现场的用户可以在应用中观看I/O大会的相关视频。　　去年的I/O大会上，谷歌推出了AndroidL预览版，Android系统无论在界面设计与功能方面都做出了很大改变，不知道今年的谷歌大会由会给我们带来哪些惊喜。";

    @Autowired
    private ITopicService topicService;

    @Test
    public void test01() {
        System.out.println("关键词短语提取结果：\n" + topicService.abstractKeywords(text));
    }
}