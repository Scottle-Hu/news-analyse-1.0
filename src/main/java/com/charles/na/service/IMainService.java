package com.charles.na.service;

import com.charles.na.soa.INewsSOAService;
import com.charles.na.spider.service.ISpiderService;

/**
 * 统一的分析类
 *
 * @author huqj
 */
public interface IMainService {

    void analyse();

    void setDate(String date);

    INewsSOAService getNewsSOAService();

    ITrackService getTrackService();

    IResultService getResultService();

    ISpiderService getSpiderService();

}
