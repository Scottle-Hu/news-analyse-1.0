package com.charles.na.service.impl;

import com.charles.na.service.IPersonService;
import com.charles.na.utils.HttpUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;


/**
 * @author huqj
 */
@Service("personService")
public class PersonServiceImpl implements IPersonService {

    private Logger LOGGER = Logger.getLogger(PersonServiceImpl.class);

    private String url = "https://baike.baidu.com/item/";

    private static Set<String> personName = new HashSet<String>();

    private static Set<String> nonPersonName = new HashSet<String>();

    /**
     * 作为人名百科的网页标识内容
     */
    private String PEOPLE_SINGAL1 = "<dt class=\"basicInfo-item name\">出生地</dt>";
    private String PEOPLE_SINGAL2 = "<dt class=\"basicInfo-item name\">出生日期</dt>";
    private String PEOPLE_SINGAL3 = "<dt class=\"basicInfo-item name\">籍    贯</dt>";
    private String PEOPLE_SINGAL4 = "<dt class=\"basicInfo-item name\">毕业院校</dt>";

    public boolean isFamousPerson(String people) {
        //先查缓存
        if (personName.contains(people)) {
            LOGGER.info("缓存中存在人名：" + people);
            return true;
        }
        if (nonPersonName.contains(people)) {
            LOGGER.info("非人名缓存中存在：" + people);
            return false;
        }
        //再调百科
        String finalUrl = url + people;
        String page = HttpUtil.getRequest(finalUrl, null);
        if (StringUtils.isEmpty(page)) {
            LOGGER.info("页面内容为空:" + finalUrl);
            return false;
        }
        if (page.contains(PEOPLE_SINGAL1) || page.contains(PEOPLE_SINGAL2)
                || page.contains(PEOPLE_SINGAL3) || page.contains(PEOPLE_SINGAL4)) {
            LOGGER.info("存在人名：" + people);
            personName.add(people);
            return true;
        } else {
            LOGGER.info("不存在人名：" + people);
            nonPersonName.add(people);
            return false;
        }
    }
}
