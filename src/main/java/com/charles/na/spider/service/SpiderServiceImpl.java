package com.charles.na.spider.service;

import com.charles.na.spider.plan.SpiderPlan;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class SpiderServiceImpl implements ISpiderService {

    /**
     * 所有待执行的爬虫计划，爬虫计划只要实现SpiderPlan接口即可交由此处运行
     */
    private List<SpiderPlan> planList;

    @Resource
    private SpiderPlan sinaPlan;

    /**
     * 可扩展的添加爬虫计划
     */
    @PostConstruct
    public void initPlan() {
        //TODO 此处的功能可以交给spring配置文件去做,目前只有一个爬虫（新浪），简单实现
        planList = new ArrayList<>();
        planList.add(sinaPlan);
    }

    @Override
    public void collect() {
        List<Integer> threads = new ArrayList<>();
        for (SpiderPlan plan : planList) {
            threads.add(1);
            plan.setThreads(threads);
            plan.start();
        }
        SpiderPlan.waitOrNotify(threads);
    }
}
