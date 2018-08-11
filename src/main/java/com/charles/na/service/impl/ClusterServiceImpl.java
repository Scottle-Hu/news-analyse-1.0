package com.charles.na.service.impl;

import com.charles.na.mapper.ClusterMapper;
import com.charles.na.mapper.DocumentVectorMapper;
import com.charles.na.model.Cluster;
import com.charles.na.model.DocumentVector;
import com.charles.na.model.News;
import com.charles.na.service.IClusterService;
import com.charles.na.service.INewsService;
import com.charles.na.service.IVectorService;
import com.charles.na.soa.impl.thread.CalKMeansThread;
import com.charles.na.soa.impl.thread.CalSimilarityThread;
import com.charles.na.utils.DocumentVector2MapUtil;
import com.charles.na.utils.IDUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author huqj
 * @description 聚类服务实现类
 * @create 2018/4/16
 * @rewrite 2018/5/23
 * @see IClusterService
 */
@Service("clusterService")
public class ClusterServiceImpl implements IClusterService {

    private Logger LOGGER = Logger.getLogger(ClusterServiceImpl.class);

    //注意：在平面距离聚类中，T1>t2，说的是距离，但是在这里向量距离越小，反而相似度越高
    private static double T1 = 0.2;

    private static double T2 = 0.4;

    private static final int PAGE_SIZE = 100;

    private static final int K_MEANS_MAX = 50;   //k-means聚类的最大迭代次数

    private static final int CLUSTER_MIN_NUM = 3;  //若某个聚类元素个数小于该值，则舍弃该聚类

    private static final int concurrentCalNum = 100;  //canopy并发计算的个数

    private static final int kMeansConcurrentCalNum = 100;  //kmeans并发计算的个数

    private static double allowedMaxCenterDiff = 10e-3;  //k-means中允许的最大中心变化量

    private String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    @Autowired
    private DocumentVectorMapper documentVectorMapper;

    @Autowired
    private IVectorService vectorService;

    @Autowired
    private ClusterMapper clusterMapper;

    /**
     * @return 粗聚类的初始中心（不需要精确中心）组成的列表
     * @description 对预处理得到的文本向量进行canopy聚类
     */
    public Map<DocumentVector, Set<DocumentVector>> canopy(Set<Long> removeId) {
        if (removeId == null) {
            removeId = new HashSet<Long>();
        }
        //分页读取新闻数据并进行canopy聚类
        try {
            //从数据库读取当天所有未处理的文本向量
            List<DocumentVector> vectorList = documentVectorMapper.findAllByDate(today);
            //聚类：中心向量 -> 该聚类的向量集合
            Map<DocumentVector, Set<DocumentVector>> cluster = new HashMap<DocumentVector, Set<DocumentVector>>();
            int iterateNum = 0;
            while (vectorList.size() > 0) {  //迭代直到所有点都被移除
                iterateNum++;
                LOGGER.info("已经迭代次数：" + iterateNum);
                LOGGER.info("剩余向量数：" + vectorList.size());
                //先找到第一个没有移除的点作为聚类中心
                DocumentVector c = vectorList.get(0);
                vectorList.remove(0);
                cluster.put(c, new HashSet<DocumentVector>());
                cluster.get(c).add(c);
                Set<Long> ids = new HashSet<Long>();
                //并发计算相似度
                concurrentCalSimilarity(c, vectorList, vectorService, cluster, ids);
                Iterator<DocumentVector> iterator = vectorList.iterator();
                while (iterator.hasNext()) {
                    if (ids.contains(iterator.next().getId())) {
                        iterator.remove();
                    }
                }
            }
            Set<Long> distinctIds = new HashSet<Long>();
            //去除小众聚类
            int delNum = 0;
            Iterator<Map.Entry<DocumentVector, Set<DocumentVector>>> it = cluster.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<DocumentVector, Set<DocumentVector>> e = it.next();
                if (e.getValue().size() < CLUSTER_MIN_NUM) {
                    for (DocumentVector td : e.getValue()) {
                        removeId.add(td.getId());
                    }
                    it.remove();
                    delNum++;
                } else {
                    for (DocumentVector td : e.getValue()) {
                        distinctIds.add(td.getId());
                    }
                }
            }
            LOGGER.info("一共删除小众聚类：" + delNum);
            LOGGER.info("待细聚类的向量个数：" + distinctIds.size());
            return cluster;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("canopy聚类的过程中出现未知问题。");
        }
        return null;
    }

    /**
     * 并发计算其余点到某个点的距离以加快canopy速度
     */
    private void concurrentCalSimilarity(DocumentVector c, List<DocumentVector> vectorList, IVectorService vectorService,
                                         Map<DocumentVector, Set<DocumentVector>> cluster, Set<Long> removeId) {
        //记录当前线程数目
        List<Integer> threads = new ArrayList<Integer>();
        int size = vectorList.size();
        int threadNum = size / concurrentCalNum + (size % concurrentCalNum == 0 ? 0 : 1);  //每个线程计算100个
        for (int i = 0; i < threadNum; i++) {
            int start = i * concurrentCalNum;
            int end = (i + 1) * concurrentCalNum;
            end = end > size ? size : end;
            threads.add(1);
            new CalSimilarityThread(vectorList, start, end, c, cluster, removeId, vectorService, threads).start();
        }
        waitOrNotify(c, threads);  //!!不能直接wait，否则万一子线程非常快就会锁住
    }

    /**
     * @param canopy 粗聚类结果
     * @description 以canopy得到的初始结果为迭代起点细化聚类，通过LDA算法提取主题，
     * 并存入数据库的t_cluster表和t_topic表
     */
    public void kMeans(Map<DocumentVector, Set<DocumentVector>> canopy, Set<Long> removeId) {
        try {
            Map<DocumentVector, Set<DocumentVector>> cluster = new HashMap<DocumentVector, Set<DocumentVector>>();
            //init
            for (DocumentVector dv : canopy.keySet()) {
                cluster.put(dv, new HashSet<DocumentVector>());
                cluster.get(dv).add(dv);
            }
            Set<Long> distinctIds = new HashSet<Long>();
            List<DocumentVector> vectorList = new ArrayList<DocumentVector>();
            for (Map.Entry<DocumentVector, Set<DocumentVector>> e : canopy.entrySet()) {
                for (DocumentVector d : e.getValue()) {
                    if (!distinctIds.contains(d.getId())) {
                        vectorList.add(d);
                        distinctIds.add(d.getId());
                    }
                }
            }
            LOGGER.info("k-means聚类初始数目：" + vectorList.size());
            boolean goOn = true;
            int iterateNum = 0;
            while (goOn && iterateNum < K_MEANS_MAX) {
                iterateNum++;
                LOGGER.info("k-means聚类迭代次数：" + iterateNum);
                //并发计算新的聚类中心，以提高速度
                concurrentCalKMeans(vectorList, cluster, vectorService, removeId, canopy);
                //重新计算簇心
                goOn = reCalClusterCenter(cluster, canopy);
            }
            //剔除较小的聚类
            int delNum = 0;
            Iterator<Map.Entry<DocumentVector, Set<DocumentVector>>> it = cluster.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<DocumentVector, Set<DocumentVector>> e = it.next();
                if (e.getValue().size() < CLUSTER_MIN_NUM) {
                    it.remove();
                    delNum++;
                }
            }
            LOGGER.info("删除小众聚类个数：" + delNum);
            //聚类信息存入数据库
            for (Map.Entry<DocumentVector, Set<DocumentVector>> e : cluster.entrySet()) {
                //TODO 今晚遇到cluster主键重复的问题，先简单处理，明天再细看
                Set<Cluster> clusterSet = new HashSet<Cluster>();
                for (DocumentVector d : e.getValue()) {
                    Cluster c = new Cluster();
                    c.setId(e.getKey().getId() + "");
                    c.setNewsId(d.getNewsId());
                    c.setDate(today);
                    clusterSet.add(c);
                }
                clusterMapper.insertMany(new ArrayList<>(clusterSet));
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("在进行k-means聚类的时候出现未知问题。");
        }
    }

    /**
     * 并发计算k-means的迭代过程
     */
    private void concurrentCalKMeans(List<DocumentVector> vectorList, Map<DocumentVector, Set<DocumentVector>> cluster,
                                     IVectorService vectorService, Set<Long> removeId, Map<DocumentVector, Set<DocumentVector>> canopy) {
        //TODO 倒排 点->canopy中心，加快kmeans寻找局部聚类点的速度

        //记录当前线程数目
        List<Integer> threads = new ArrayList<Integer>();
        int size = vectorList.size();
        int threadNum = size / kMeansConcurrentCalNum + (size % kMeansConcurrentCalNum == 0 ? 0 : 1);  //每个线程计算30个点
        for (int i = 0; i < threadNum; i++) {
            int start = i * kMeansConcurrentCalNum;
            int end = (i + 1) * kMeansConcurrentCalNum;
            end = end > size ? size : end;
            threads.add(1);
            new CalKMeansThread(vectorList, start, end, cluster, removeId, vectorService, threads, canopy).start();
        }
        waitOrNotify(vectorList, threads);
    }

    /**
     * @param cluster
     * @param canopy
     * @return 是否有簇心发生改变
     * @description 重新计算聚簇中心的私有方法
     */
    private boolean reCalClusterCenter(Map<DocumentVector, Set<DocumentVector>> cluster, Map<DocumentVector, Set<DocumentVector>> canopy) {
        boolean changed = false;
        HashSet<Map<DocumentVector, DocumentVector>> waitToChangeCenter =
                new HashSet<Map<DocumentVector, DocumentVector>>();
        for (Map.Entry<DocumentVector, Set<DocumentVector>> e : cluster.entrySet()) {
            Set<DocumentVector> vectors = e.getValue();
            HashSet<Map<String, Double>> maps = new HashSet<Map<String, Double>>();
            HashSet<String> words = new HashSet<String>();
            for (DocumentVector dv : vectors) {
                Map<String, Double> tmpMap = DocumentVector2MapUtil.convertDocumentVector2Map(dv);
                maps.add(tmpMap);
                words.addAll(tmpMap.keySet());
            }
            for (Map<String, Double> map : maps) {
                DocumentVector2MapUtil.fillVector(map, words);
            }
            //计算中心
            Map<String, Double> center = DocumentVector2MapUtil.calCenterOfVectors(maps);
            DocumentVector newCenter = DocumentVector2MapUtil.convertMap2DocumentVector(center);
            DocumentVector oldCenter = e.getKey();
            //判断是否改变
            double simBetweenOldAndNewCenter = vectorService.calSimilarity(newCenter, oldCenter);
            if (simBetweenOldAndNewCenter > allowedMaxCenterDiff) {
                changed = true;
                Map<DocumentVector, DocumentVector> m = new HashMap<DocumentVector, DocumentVector>();
                m.put(oldCenter, newCenter);
                waitToChangeCenter.add(m);
            }
        }
        //替换key，即聚类中心，在此处替换是因为上面的迭代过程不能做修改，否则触发fail-fast机制
        for (Map<DocumentVector, DocumentVector> pair : waitToChangeCenter) {
            for (Map.Entry<DocumentVector, DocumentVector> e : pair.entrySet()) {
                DocumentVector old = e.getKey();
                DocumentVector _new = e.getValue();
                cluster.put(_new, cluster.get(old));
                cluster.remove(old);
                //同步替换canopy
                canopy.put(_new, canopy.get(old));
                canopy.remove(old);
            }
        }
        return changed;
    }

    public void setDate(String date) {
        this.today = date;
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
        } else {
            synchronized (obj) {
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 测试方法，将canopy聚类中心写入文件
     */
    @Deprecated
    private void saveCanopyResult(Map<DocumentVector, Set<DocumentVector>> canopy, String filename) {
        File f = new File(filename);
        OutputStream out = null;
        try {
            out = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (DocumentVector dv : canopy.keySet()) {
            String line = dv.getNewsId() + ":" + dv.getVector() + ":" + canopy.get(dv).size() + "\r\n";
            try {
                out.write(line.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
