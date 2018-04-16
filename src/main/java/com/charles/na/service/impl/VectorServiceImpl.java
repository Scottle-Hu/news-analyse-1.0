package com.charles.na.service.impl;

import com.charles.na.mapper.SynonymMapper;
import com.charles.na.model.DocumentVector;
import com.charles.na.service.IVectorService;
import com.charles.na.soa.impl.thread.BuildDocumentVectorThread;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Charles
 * @create 2018/3/29
 * @description 向量服务类
 * @since 1.0
 */
@Service("vectorService")
public class VectorServiceImpl implements IVectorService {

    private Logger LOGGER = Logger.getLogger(VectorServiceImpl.class);

    @Resource
    private SynonymMapper synonymMapper;

    /**
     * @param v1
     * @param v2
     * @return
     * @description 计算两个文本向量之间的相似度
     */
    public double calSimilarity(DocumentVector v1, DocumentVector v2) {
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
