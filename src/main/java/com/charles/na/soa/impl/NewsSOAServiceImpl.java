package com.charles.na.soa.impl;

import com.charles.na.common.Constant;
import com.charles.na.mapper.DocumentVectorMapper;
import com.charles.na.mapper.OptRecordMapper;
import com.charles.na.soa.INewsSOAService;
import com.charles.na.service.INewsService;
import com.charles.na.soa.impl.thread.BuildDocumentVectorThread;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 新闻相关分析soa服务实现
 * @since 1.0
 */
@Service("newsSOAService")
public class NewsSOAServiceImpl implements INewsSOAService {

    private Logger LOGGER = Logger.getLogger(NewsSOAServiceImpl.class);

    /**
     * 线程池，用于分页分别执行任务（注意：用在数据之间没有关联的情况下）
     */
    private static ExecutorService pools = Executors.newFixedThreadPool(Constant.MAX_THREAD_NUM);

    /**
     * 记录当前正在分页执行文本向量建立任务的线程数目
     */
    public static volatile int currentVectorThreadNum;

    @Resource
    private INewsService newsService;

    @Resource
    private DocumentVectorMapper documentVectorMapper;

    @Resource
    private OptRecordMapper optRecordMapper;

    private static int PAGE_SIZE = 100;

    public boolean vector() {
        if (currentVectorThreadNum > 0) { //上次的任务未执行完成
            return false;
        }
        try {
            long start = System.currentTimeMillis();
            int newsNum = newsService.queryNum();
            int pageNum = newsNum / PAGE_SIZE + (newsNum % PAGE_SIZE == 0 ? 0 : 1);
            currentVectorThreadNum = pageNum;
            for (int n = 0; n < pageNum; n++) {
                Map<String, Integer> pageInfo = new HashMap<String, Integer>();
                pageInfo.put("pageNo", n * PAGE_SIZE);
                pageInfo.put("pageSize", PAGE_SIZE);
                BuildDocumentVectorThread thread = new BuildDocumentVectorThread(pageInfo);
                thread.setDocumentVectorMapper(documentVectorMapper);
                thread.setNewsService(newsService);
                thread.setOptRecordMapper(optRecordMapper);
                //提交线程池运行
                pools.execute(thread);
            }
            // TODO 创建统计线程，统计运行时间

//            //测试的时候为了防止程序整体退出
//            new Scanner(System.in).next();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("文档向量创建处理失败");
            return false;
        }
    }

    public void cluster() {
        //TODO 文本聚类实现

    }

    @PreDestroy
    public void destroy() {
        //关闭线程池
        pools.shutdown();
    }

}
