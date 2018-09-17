package com.charles.na.service.impl;

import com.charles.na.service.IMainService;
import com.charles.na.service.IResultService;
import com.charles.na.service.ITrackService;
import com.charles.na.soa.INewsSOAService;
import com.charles.na.spider.service.ISpiderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author huqj
 */
@Service("mainService")
public class MainServiceImpl implements IMainService {

    @Resource
    private INewsSOAService newsSOAService;

    @Resource
    private ITrackService trackService;

    @Resource
    private IResultService resultService;

    @Resource
    private ISpiderService spiderService;

    public void analyse() {
        //开启爬虫
        spiderService.collect();
        //创建文档向量
        newsSOAService.vector();
        //创建文档向量需要等待五分钟，因为开了新线程就没管了
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newsSOAService.cluster();
        newsSOAService.topic();
        trackService.track();
        resultService.convert2result();
    }

    //自己设置日期
    public void setDate(String date) {
        if (!StringUtils.isEmpty(date)) {
            newsSOAService.setDate(date);
            trackService.setDate(date);
            resultService.setDate(date);
            spiderService.setDate(date);
        }
    }

    public INewsSOAService getNewsSOAService() {
        return newsSOAService;
    }

    public ITrackService getTrackService() {
        return trackService;
    }

    public IResultService getResultService() {
        return resultService;
    }

    @Override
    public ISpiderService getSpiderService() {
        return spiderService;
    }
}
