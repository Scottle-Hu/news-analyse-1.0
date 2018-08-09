package com.charles.na.soa.impl.thread;

import com.charles.na.model.DocumentVector;
import com.charles.na.service.IVectorService;
import com.charles.na.service.impl.ClusterServiceImpl;

import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * @author huqj
 */
public class CalSimilarityThread extends Thread {

    /**
     * 待计算列表
     */
    private List<DocumentVector> vectorList;
    /**
     * 分配计算向量的起始位置
     */
    private int start;
    private int end;

    private DocumentVector c;

    /**
     * 聚类结果存放
     */
    private Map<DocumentVector, Set<DocumentVector>> cluster;

    /**
     * 去重id
     */
    private Set<Long> removeId;

    private List<Integer> threads;

    private IVectorService vectorService;

    private double T1 = 0.25;

    private double T2 = 0.4;

    public CalSimilarityThread(List<DocumentVector> vectorList, int start, int end, DocumentVector c,
                               Map<DocumentVector, Set<DocumentVector>> cluster, Set<Long> removeId, IVectorService vectorService, List<Integer> threads) {
        this.vectorList = vectorList;
        this.start = start;
        this.end = end;
        this.cluster = cluster;
        this.removeId = removeId;
        this.vectorService = vectorService;
        this.threads = threads;
        this.c = c;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            //System.out.println(start + ":" );
            DocumentVector dv = vectorList.get(i);
            double result = vectorService.calSimilarity(c, dv);
            if (result >= T1) {
                synchronized (cluster) {  //防止线程不安全
                    cluster.get(c).add(dv);
                }
            }
            if (result >= T2) {
                removeId.add(dv.getId());
            }
        }
        synchronized (threads) {
            threads.remove(0);
        }
        ClusterServiceImpl.waitOrNotify(c, threads);
    }
}
