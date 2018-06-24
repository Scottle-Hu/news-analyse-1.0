package com.charles.na.service.impl;

import com.charles.na.service.IMainService;
import com.charles.na.service.IResultService;
import com.charles.na.service.ITrackService;
import com.charles.na.soa.INewsSOAService;
import org.springframework.stereotype.Service;

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

    public void analyse() {
        newsSOAService.vector();
        //创建文档向量需要等待十分钟，因为开了新线程就没管了
        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newsSOAService.cluster();
        newsSOAService.topic();
        trackService.track();
        resultService.convert2result();
    }
}
