package com.charles.na.web.service;

import com.charles.na.model.EventResult;
import com.charles.na.web.model.MainPageInfo;

/**
 * 根据时间段查询、排序热点的业务
 *
 * @author huqj
 */
public interface IQueryService {

    /**
     * 根据起止时间查询热点
     *
     * @param start
     * @param end
     * @return
     */
    MainPageInfo queryByDuring(String start, String end);

    /**
     * 根据事件的时间和id查询事件详情
     *
     * @param date
     * @param id
     * @return
     */
    EventResult findEventResultByDateAndId(String date, String id);
    
}
