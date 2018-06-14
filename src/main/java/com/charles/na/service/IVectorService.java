package com.charles.na.service;

import com.charles.na.model.DocumentVector;

import java.util.Map;

/**
 * @author huqj
 * @create 2018/3/29
 * @description 文本向量计算服务接口
 * @since 1.0
 */
public interface IVectorService {

    double calSimilarity(DocumentVector v1, DocumentVector v2);

    double simpleCalSimilarity(DocumentVector v1, DocumentVector v2);

    double calSynonym(String word1, String word2);
}
