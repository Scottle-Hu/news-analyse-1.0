package com.charles.na.service;

import java.util.Map;

/**
 * @author 李紫宸
 * @create 2018/3/29
 * @description 文本向量计算服务接口
 * @since 1.0
 */
public interface IVectorService {

    double calSimilarity(Map<String, Integer> v1, Map<String, Integer> v2);

    double calSynonym(String word1, String word2);
}
