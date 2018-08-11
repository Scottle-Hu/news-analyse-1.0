package com.charles.na.soa.impl.thread;

import com.charles.na.model.DocumentVector;
import com.charles.na.service.IVectorService;
import com.charles.na.service.impl.ClusterServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author huqj
 */
public class CalKMeansThread extends Thread {

    /**
     * 待计算列表
     */
    private List<DocumentVector> vectorList;
    /**
     * 分配计算向量的起始位置
     */
    private int start;
    private int end;

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

    private Map<DocumentVector, Set<DocumentVector>> canopy;

    public CalKMeansThread(List<DocumentVector> vectorList, int start, int end, Map<DocumentVector, Set<DocumentVector>> cluster,
                           Set<Long> removeId, IVectorService vectorService, List<Integer> threads, Map<DocumentVector, Set<DocumentVector>> canopy) {
        this.vectorList = vectorList;
        this.start = start;
        this.end = end;
        this.cluster = cluster;
        this.removeId = removeId;
        this.vectorService = vectorService;
        this.threads = threads;
        this.canopy = canopy;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            DocumentVector dv = vectorList.get(i);
            if (removeId.contains(dv.getId())) {  //排除小众聚类
                continue;
            }
            double sim = Double.MIN_VALUE;
            DocumentVector tmpCenter = null;
            Set<DocumentVector> subCenters = getSubCenters(canopy, dv);
            for (DocumentVector c : subCenters) {  //寻找最近的聚簇中心
                double tmpSim = vectorService.calSimilarity(dv, c);
                if (tmpSim > sim) {
                    sim = tmpSim;
                    tmpCenter = c;
                }
            }
            if (tmpCenter != null) {
                //从原先聚类中移除
                DocumentVector oldCenter = null;
                for (Map.Entry<DocumentVector, Set<DocumentVector>> e : cluster.entrySet()) {
                    if (e.getValue().contains(dv)) {
                        oldCenter = e.getKey();
                        break;
                    }
                }
                synchronized (cluster) {
                    if (oldCenter != null) {
                        cluster.get(oldCenter).remove(dv);
                    }
                    //添加到新聚类中
                    cluster.get(tmpCenter).add(dv);
                }
            }
        }
        synchronized (threads) {  //防止fail-fast
            threads.remove(0);
        }
        System.out.println("剩余线程数目：" + threads.size());
        ClusterServiceImpl.waitOrNotify(vectorList, threads);
    }

    /**
     * 获取某个向量在canopy阶段所属的canopy的所有向量
     *
     * @param canopy
     * @param dv
     * @return
     */
    private Set<DocumentVector> getSubCenters(Map<DocumentVector, Set<DocumentVector>> canopy, DocumentVector dv) {
        Set<DocumentVector> res = new HashSet<DocumentVector>();
        for (Map.Entry<DocumentVector, Set<DocumentVector>> e : canopy.entrySet()) {
            for (DocumentVector d : e.getValue()) {
                if (d.getNewsId().equals(dv.getNewsId())) {
                    res.add(e.getKey());
                    break;
                }
            }
        }
        return res;
    }
}
