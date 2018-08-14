package com.charles.na.soa.impl;

import com.charles.na.common.Constant;
import com.charles.na.mapper.ClusterMapper;
import com.charles.na.mapper.DocumentVectorMapper;
import com.charles.na.mapper.OptRecordMapper;
import com.charles.na.mapper.TopicMapper;
import com.charles.na.model.Cluster;
import com.charles.na.model.DocumentVector;
import com.charles.na.model.News;
import com.charles.na.model.Topic;
import com.charles.na.service.IClusterService;
import com.charles.na.service.ITopicService;
import com.charles.na.soa.INewsSOAService;
import com.charles.na.service.INewsService;
import com.charles.na.soa.impl.thread.BuildDocumentVectorThread;
import com.charles.na.utils.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private static ExecutorService pools = Executors.newCachedThreadPool();

    private String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    @Resource
    private INewsService newsService;

    @Resource
    private ITopicService topicService;

    @Resource
    private IClusterService clusterService;

    @Resource
    private DocumentVectorMapper documentVectorMapper;

    @Resource
    private OptRecordMapper optRecordMapper;

    @Resource
    private TopicMapper topicMapper;

    @Resource
    private ClusterMapper clusterMapper;

    private static int PAGE_SIZE = 100;

    public boolean vector() {
        try {
            List<Integer> threads = new ArrayList<>();
            int newsNum = newsService.queryNum(today);
            int pageNum = newsNum / PAGE_SIZE + (newsNum % PAGE_SIZE == 0 ? 0 : 1);
            for (int n = 0; n < pageNum; n++) {
                Map<String, Object> pageInfo = new HashMap<String, Object>();
                pageInfo.put("pageNo", n * PAGE_SIZE);
                pageInfo.put("pageSize", PAGE_SIZE);
                pageInfo.put("date", today);
                BuildDocumentVectorThread thread = new BuildDocumentVectorThread(pageInfo);
                thread.setDocumentVectorMapper(documentVectorMapper);
                thread.setNewsService(newsService);
                thread.setOptRecordMapper(optRecordMapper);
                threads.add(1);
                thread.setThreads(threads);
                //提交线程池运行
                pools.execute(thread);
            }
            waitOrNotify(threads, threads);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("文档向量创建处理失败");
            return false;
        }
    }

    public void cluster() {
        long start = System.currentTimeMillis();
        Set<Long> removeId = new HashSet<Long>();
        //canopy聚类
        Map<DocumentVector, Set<DocumentVector>> canopy = clusterService.canopy(removeId);
        LOGGER.info("canopy聚类个数：" + canopy.size());
        LOGGER.info("canopy耗时：" + TimeUtil.convertMillis2String(System.currentTimeMillis() - start));
        //k-means聚类
        clusterService.kMeans(canopy, removeId);

    }

    public void topic() {
        //获取当天所有聚类id
        List<Cluster> clusterList = clusterMapper.findByDate(today);
        Set<String> idSet = new HashSet<String>();
        for (Cluster cluster : clusterList) {
            idSet.add(cluster.getId());
        }
        //聚类id->新闻id列表 的映射
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (String id : idSet) {
            map.put(id, new ArrayList<String>());
            Iterator<Cluster> iterator = clusterList.iterator();
            while (iterator.hasNext()) {
                Cluster cluster = iterator.next();
                if (cluster.getId().equals(id)) {
                    map.get(id).add(cluster.getNewsId());
                    iterator.remove();
                }
            }
        }
        //提取主题，计算热度
        //TODO 热度的计算先简单实现
        int totalNewsNum = 0;
        for (Map.Entry<String, List<String>> e : map.entrySet()) {
            totalNewsNum += e.getValue().size();
        }
        for (Map.Entry<String, List<String>> e : map.entrySet()) {
            List<String> newsIds = e.getValue();
            Topic topic = new Topic();
            topic.setId(e.getKey());
            topic.setDate(today);
            topic.setNewsNum(newsIds.size());
            topic.setHot((int) (((double) newsIds.size() / totalNewsNum) * 100));
            StringBuilder titles = new StringBuilder();
            for (String newsId : newsIds) {
                News news = newsService.findById(newsId);
                if (news != null && news.getTitle() != null) {
                    titles.append(news.getTitle());
                    titles.append(" ");
                }
            }
            List<String> topicNameList = topicService.abstractKeywords(titles.toString());
            String topicName = "";
            for (String s : topicNameList) {
                topicName += s + " ";
            }
            if (StringUtils.isBlank(topicName) || topicName.length() < 2) {
                topicName = "关键词缺失";
            }
            if (topicName.length() > 10) {
                topicName = topicName.substring(0, 10) + "...";
            }
            topic.setTopic(topicName);
            topicMapper.saveTopic(topic);
        }
    }

    public void setDate(String date) {
        this.today = date;
        clusterService.setDate(date);
    }

    /**
     * 多线程计算的时候，当一个线程计算完成后，要么等待，要么唤醒其他线程
     *
     * @param obj
     * @param threads
     */
    public static void waitOrNotify(Object obj, List<Integer> threads) {
        String name = Thread.currentThread().getName();
        if (threads.size() == 0) {
            synchronized (obj) {
                obj.notifyAll();
            }
            System.out.println(name + " notifyAll");
        } else {
            System.out.println(name + " wait");
            synchronized (obj) {
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PreDestroy
    public void destroy() {
        //关闭线程池
        if (pools != null) {
            pools.shutdown();
        }
    }

}
