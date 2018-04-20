package com.charles.na.service.impl;

import com.charles.na.mapper.DocumentVectorMapper;
import com.charles.na.model.DocumentVector;
import com.charles.na.service.IClusterService;
import com.charles.na.service.IVectorService;
import com.charles.na.utils.DocumentVector2MapUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author huqj
 * @description 聚类服务实现类
 * @create 2018/4/16
 * @see IClusterService
 */
@Service("clusterService")
public class ClusterServiceImpl implements IClusterService {

    private Logger LOGGER = Logger.getLogger(ClusterServiceImpl.class);

    //注意：在平面距离聚类中，T1>t2，说的是距离，但是在这里向量距离越小，反而相似度越高
    private static double T1 = 0.05;

    private static double T2 = 0.15;

    private static int PAGE_SIZE = 100;

    private static int K_MEANS_MAX = 50;   //k-means聚类的最大迭代次数

    @Autowired
    private DocumentVectorMapper documentVectorMapper;

    @Autowired
    private IVectorService vectorService;

    /**
     * @return 粗聚类的初始中心（不需要精确中心）组成的列表
     * @description 对预处理得到的文本向量进行canopy聚类
     */
    public List<DocumentVector> canopy() {
        //分页读取新闻数据并进行canopy聚类
        try {
            int newsTotalNum = documentVectorMapper.queryNum();
            System.out.println("新闻总数:" + newsTotalNum);
            //聚类：中心向量 -> 该聚类的向量集合
            Map<DocumentVector, Set<DocumentVector>> cluster = new HashMap<DocumentVector, Set<DocumentVector>>();
            //聚类的中心文本向量的id
            Set<String> removeSet = new HashSet<String>();
            int iterateNum = 0;
            while (removeSet.size() < newsTotalNum) {  //迭代直到所有点都被移除
                iterateNum++;
                System.out.println("已经迭代次数：" + iterateNum);
                System.out.println("已经移除的点：" + removeSet.size());
                //先找到第一个没有移除的点作为聚类中心
                DocumentVector c = null;
                Map<String, Integer> pageInfo = new HashMap<String, Integer>();
                pageInfo.put("pageNo", 0);
                pageInfo.put("pageSize", PAGE_SIZE);
                List<DocumentVector> vectorList = documentVectorMapper.findByPageInfo(pageInfo);
                while (vectorList != null && vectorList.size() > 0) {
                    boolean foundCenter = false;
                    for (DocumentVector v : vectorList) {
                        String id = v.getId();
                        if (!removeSet.contains(id)) {
                            c = v;
                            removeSet.add(id);
                            foundCenter = true;
                            break;
                        }
                    }
                    if (foundCenter) {
                        break;
                    }
                    //换页
                    pageInfo.put("pageNo", pageInfo.get("pageNo") + PAGE_SIZE);
                    vectorList = documentVectorMapper.findByPageInfo(pageInfo);
                }
                if (c == null) {
                    LOGGER.error("取不到中心，这是不应该出现的情况！");
                    break;
                }
                cluster.put(c, new HashSet<DocumentVector>());
                cluster.get(c).add(c);
                //再次分页遍历元素
                pageInfo.put("pageNo", 0);
                pageInfo.put("pageSize", PAGE_SIZE);
                vectorList = documentVectorMapper.findByPageInfo(pageInfo);
                while (vectorList != null && vectorList.size() > 0) {
                    System.out.println("分页：" + pageInfo.get("pageNo"));
                    for (DocumentVector dv : vectorList) {
                        if (removeSet.contains(dv.getId())) {  //已被移除
                            continue;
                        }
                        double result = vectorService.calSimilarity(c, dv);
                        if (result >= T1) {
                            System.out.println("相似度：" + result);
                            cluster.get(c).add(dv);
                        }
                        if (result >= T2) {
                            System.out.println("较高相似度：" + result);
                            removeSet.add(dv.getId());
                        }
                    }
                    //换页
                    pageInfo.put("pageNo", pageInfo.get("pageNo") + PAGE_SIZE);
                    vectorList = documentVectorMapper.findByPageInfo(pageInfo);
                }
            }
            //TODO 筛选出个数小于2个的聚类
            List<DocumentVector> result = new ArrayList<DocumentVector>();
            result.addAll(cluster.keySet());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("canopy聚类的过程中出现未知问题。");
        }
        return null;
    }

    /**
     * @param centerList 初始中心
     * @description 以canopy得到的初始结果为迭代起点细化聚类，通过LDA算法提取主题，
     * 并存入数据库的t_cluster表和t_topic表
     */
    public void kMeans(List<DocumentVector> centerList) {
        try {
            Map<DocumentVector, HashSet<DocumentVector>> cluster =
                    new HashMap<DocumentVector, HashSet<DocumentVector>>();
            //init
            for (DocumentVector dv : centerList) {
                HashSet<DocumentVector> ts = new HashSet<DocumentVector>();
                ts.add(dv);
                cluster.put(dv, ts);
            }

            boolean goOn = true;
            int iterateNum = 0;
            while (goOn && iterateNum < K_MEANS_MAX) {
                iterateNum++;

                //获取聚簇中心的集合
                Set<DocumentVector> centers = cluster.keySet();

                //分页读取文本向量
                Map<String, Integer> pageInfo = new HashMap<String, Integer>();
                pageInfo.put("pageNo", 0);
                pageInfo.put("pageSize", PAGE_SIZE);
                List<DocumentVector> vectorList = documentVectorMapper.findByPageInfo(pageInfo);
                while (vectorList != null && vectorList.size() > 0) {
                    for (DocumentVector dv : vectorList) {
                        double sim = Double.MIN_VALUE;
                        DocumentVector tmpCenter = null;
                        for (DocumentVector c : centers) {  //寻找最近的聚簇中心
                            double tmpSim = vectorService.calSimilarity(dv, c);
                            if (tmpSim > sim) {
                                sim = tmpSim;
                                tmpCenter = c;
                            }
                        }
                        if (tmpCenter != null) {
                            cluster.get(tmpCenter).add(dv);
                        }

                    }
                    //换页
                    pageInfo.put("pageNo", pageInfo.get("pageNo") + PAGE_SIZE);
                    vectorList = documentVectorMapper.findByPageInfo(pageInfo);
                }

                //重新计算簇心
                goOn = reCalClusterCenter(cluster);
            }

            //TODO 提取每个聚类的主题

            //TODO 存入数据库

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("在进行k-means聚类的时候出现未知问题。");
        }
    }

    /**
     * @param cluster
     * @return 是否有簇心发生改变
     * @description 重新计算聚簇中心的私有方法
     */
    private boolean reCalClusterCenter(Map<DocumentVector, HashSet<DocumentVector>> cluster) {
        boolean changed = false;
        HashSet<Map<DocumentVector, DocumentVector>> waitToChangeCenter =
                new HashSet<Map<DocumentVector, DocumentVector>>();
        for (Map.Entry<DocumentVector, HashSet<DocumentVector>> e : cluster.entrySet()) {
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
            //判断是否改变
            Map<String, Double> oldCenter = DocumentVector2MapUtil.convertDocumentVector2Map(e.getKey());
            for (Map.Entry<String, Double> e2 : center.entrySet()) {
                if (e2.getValue() - 0 > 10e-5) {
                    if (oldCenter.get(e2.getKey()) == null ||
                            Math.abs(oldCenter.get(e2.getKey()) - e2.getValue()) > 10e-5) {
                        changed = true;
                        Map<DocumentVector, DocumentVector> tmp = new HashMap<DocumentVector, DocumentVector>();
                        tmp.put(e.getKey(), DocumentVector2MapUtil.convertMap2DocumentVector(center));
                        waitToChangeCenter.add(tmp);
                        break;
                    }
                }
            }
        }

        //替换key，即聚类中心，在此处替换是因为上面的迭代过程不能做修改，否则触发fail-fast机制
        for (Map<DocumentVector, DocumentVector> pair : waitToChangeCenter) {
            for (Map.Entry<DocumentVector, DocumentVector> e : pair.entrySet()) {
                HashSet<DocumentVector> tmp = cluster.get(e.getKey());
                cluster.remove(e.getKey());
                cluster.put(e.getValue(), tmp);
            }
        }

        return changed;
    }


    //（很挫的测试办法）用于测试的canopy聚类方法，不要使用那么多数据
    //不过之后可以考虑用于抽样程序，取T1,T2的值
    public List<DocumentVector> canopyForTest() {
        //分页读取新闻数据并进行canopy聚类
        try {
            int newsTotalNum = documentVectorMapper.queryNum();
            System.out.println("新闻总数:" + newsTotalNum);
            //每个页面随机获取十个新闻
            List<DocumentVector> sourceVector = new ArrayList<DocumentVector>();
            Map<String, Integer> pageInfo = new HashMap<String, Integer>();
            pageInfo.put("pageNo", 0);
            pageInfo.put("pageSize", PAGE_SIZE);
            List<DocumentVector> vectorList = documentVectorMapper.findByPageInfo(pageInfo);
            while (vectorList != null && vectorList.size() > 0) {
                System.out.println("分页：" + pageInfo.get("pageNo"));
                HashSet<DocumentVector> tmpSet = new HashSet<DocumentVector>();
                if (vectorList.size() < 100) {
                    sourceVector.addAll(vectorList);
                    break;
                }
                int curSize = 100;
                for (int i = 0; i < 10; i++) {
                    int index = (int) (Math.random() * curSize);
                    tmpSet.add(vectorList.get(index));
                    vectorList.remove(index);
                    curSize--;
                }
                sourceVector.addAll(tmpSet);
                //换页
                pageInfo.put("pageNo", pageInfo.get("pageNo") + PAGE_SIZE);
                vectorList = documentVectorMapper.findByPageInfo(pageInfo);
            }
            newsTotalNum = sourceVector.size();
            System.out.println("样本向量集合大小：" + newsTotalNum);
            //聚类：中心向量 -> 该聚类的向量集合
            Map<DocumentVector, Set<DocumentVector>> cluster = new HashMap<DocumentVector, Set<DocumentVector>>();
            //聚类的中心文本向量的id
            Set<String> removeSet = new HashSet<String>();
            int iterateNum = 0;
            int aveNum = 0;
            double aveTotal = 0;
            while (removeSet.size() < newsTotalNum) {  //迭代直到所有点都被移除
                iterateNum++;
                System.out.println("已经迭代次数：" + iterateNum);
                System.out.println("已经移除的点：" + removeSet.size());
                //选取一个中心
                DocumentVector c = null;
                for (int i = 0; i < newsTotalNum; i++) {
                    String id = sourceVector.get(i).getId();
                    if (!removeSet.contains(id)) {
                        c = sourceVector.get(i);
                        removeSet.add(id);
                        break;
                    }
                }
                if (c == null) {
                    break;
                }
                cluster.put(c, new HashSet<DocumentVector>());
                cluster.get(c).add(c);
                for (int i = 0; i < newsTotalNum; i++) {
                    System.out.println("No." + (i + 1));
                    final DocumentVector dv = sourceVector.get(i);
                    if (removeSet.contains(dv.getId())) {  //已被移除
                        continue;
                    }
                    double result = vectorService.calSimilarity(c, dv);
                    aveTotal += result;
                    aveNum++;
                    if (result >= T1) {
                        System.out.println("相似度：" + result);
                        cluster.get(c).add(dv);
                    }
                    if (result >= T2) {
                        System.out.println("较高相似度：" + result);
                        removeSet.add(dv.getId());
                    }
                }

            }
            System.out.println("抽样测试平均向量相似度：" + (aveTotal / aveNum));
            List<DocumentVector> result = new ArrayList<DocumentVector>();
            result.addAll(cluster.keySet());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("canopy聚类抽样测试的过程中出现未知问题。");
        }
        return null;
    }

}