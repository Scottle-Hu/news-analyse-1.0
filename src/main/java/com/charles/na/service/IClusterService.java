package com.charles.na.service;

import com.charles.na.model.DocumentVector;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author huqj
 * @create 2018/4/16
 * @description 聚类相关方法服务接口
 */
public interface IClusterService {

    /**
     * @return 聚类类别个数
     * @description 使用canopy算法获取的各个聚类的中心
     */
    Map<DocumentVector, Set<DocumentVector>> canopy(Set<String> removeId);

    /**
     * @description 使用k-means算法细化聚类并写入数据库
     */
    void kMeans(Map<DocumentVector, Set<DocumentVector>> canopy, Set<String> removeId);

    Map<DocumentVector, Set<DocumentVector>> canopyForTest(Set<String> removeId,
                                                           List<DocumentVector> tSourceVector);

    void kMeansForTest(Set<String> removeId, List<DocumentVector> sourceVector,
                       Map<DocumentVector, Set<DocumentVector>> canopy);
}
