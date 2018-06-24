package com.charles.na.web.service;

import com.charles.na.model.Event;

import java.util.*;

/**
 * 搜索相关处理
 *
 * @author huqj
 */
public interface ISearchService {

    List<Event> searchEvent(String keywords);
}
