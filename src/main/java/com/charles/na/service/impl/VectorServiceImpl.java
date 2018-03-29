package com.charles.na.service.impl;

import com.charles.na.mapper.SynonymMapper;
import com.charles.na.service.IVectorService;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 李紫宸
 * @create 2018/3/29
 * @description 向量服务类
 * @since 1.0
 */
public class VectorServiceImpl implements IVectorService {

    @Resource
    private SynonymMapper synonymMapper;

    /**
     * @param v1
     * @param v2
     * @return
     * @description 计算两个文本向量之间的相似度
     */
    public double calSimilarity(Map<String, Integer> v1, Map<String, Integer> v2) {
        double result = 0;
        //TODO

        return result;
    }

    /**
     * @param word1
     * @param word2
     * @return
     * @description 计算两个词之间的同义程度
     */
    public double calSynonym(String word1, String word2) {
        double result = 0;
        //TODO

        return result;
    }

}
