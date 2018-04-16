package com.charles.na.service;

import com.charles.na.model.DocumentVector;

import java.util.List;

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
    List<DocumentVector> canopy();

    /**
     * @description 使用k-means算法细化聚类并写入数据库
     */
    void kMeans(List<DocumentVector> centerList);
}
