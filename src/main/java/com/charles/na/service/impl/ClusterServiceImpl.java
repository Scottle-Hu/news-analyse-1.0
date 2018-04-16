package com.charles.na.service.impl;

import com.charles.na.mapper.DocumentVectorMapper;
import com.charles.na.model.DocumentVector;
import com.charles.na.model.News;
import com.charles.na.service.IClusterService;
import com.charles.na.service.INewsService;
import com.charles.na.service.IVectorService;
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

    private static double T1 = 0.8;

    private static double T2 = 0.5;

    private static int PAGE_SIZE = 100;

    @Autowired
    private INewsService newsService;

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
            //聚类：中心向量 -> 该聚类的向量集合
            Map<DocumentVector, Set<DocumentVector>> cluster = new HashMap<DocumentVector, Set<DocumentVector>>();
            //聚类的中心文本向量的id
            Set<String> removeSet = new HashSet<String>();
            Map<String, Integer> pageInfo = new HashMap<String, Integer>();
            pageInfo.put("pageNo", 0);
            pageInfo.put("pageSize", PAGE_SIZE);
            List<DocumentVector> vectorList = documentVectorMapper.findByPageInfo(pageInfo);
            while (removeSet.size() < newsTotalNum) {  //迭代直到所有点都被移除
                while (true) {  //防止内存占满出错，分页取向量记录
                    if (vectorList == null || vectorList.size() <= 0) {
                        break;
                    }
                    int size = vectorList.size();
                    for (int i = 0; i < size; i++) {
                        final DocumentVector dv = vectorList.get(i);
                        if (removeSet.contains(vectorList.get(i).getId())) {  //已被移除
                            continue;
                        }
                        Set<DocumentVector> centers = cluster.keySet();
                        int addNum = 0;
                        for (DocumentVector c : centers) {
                            if (vectorService.calSimilarity(c, dv) <= T1) {
                                cluster.get(c).add(dv);
                                addNum++;
                            }
                            if (vectorService.calSimilarity(c, dv) <= T2) {
                                removeSet.add(dv.getId());
                            }
                        }
                        if (addNum == 0) {
                            cluster.put(dv, new HashSet<DocumentVector>() {{
                                this.add(dv);
                            }});
                        }
                    }
                    //换页
                    pageInfo.put("pageNo", pageInfo.get("pageNo") + PAGE_SIZE);
                    vectorList = documentVectorMapper.findByPageInfo(pageInfo);
                }
            }
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
        //TODO
    }
}
