package com.charles.na.spider.service;

import com.charles.na.spider.plan.SpiderPlan;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫服务【数据采集】提供者
 *
 * @author Charles
 */
@Service("spiderService")
@Log4j
public class SpiderServiceImpl implements ISpiderService {

    /**
     * 所有待执行的爬虫计划，爬虫计划只要实现SpiderPlan接口即可交由此处运行
     */
    @Autowired
    private List<SpiderPlan> planList;

    @PostConstruct
    private void init() {
        log.info("planList:" + planList);
    }

    @Override
    public void collect() {
        List<Integer> threads = new ArrayList<>();
        planList.forEach(plan -> {
            threads.add(1);
            plan.setThreads(threads);
            plan.start();
        });
        SpiderPlan.waitOrNotify(threads);
    }

    @Override
    public void setDate(String date) {
        planList.forEach(e -> {
            e.setDate(date);
        });
    }
}
