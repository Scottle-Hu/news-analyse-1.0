package com.charles.na.service.impl;

import com.charles.na.mapper.SynonymMapper;
import com.charles.na.model.DocumentVector;
import com.charles.na.service.IVectorService;
import com.charles.na.soa.impl.thread.BuildDocumentVectorThread;
import com.charles.na.utils.DocumentVector2MapUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        try {
            double result = 0;
            Map<String, Double> m1 = DocumentVector2MapUtil.convertDocumentVector2Map(v1);
            Map<String, Double> m2 = DocumentVector2MapUtil.convertDocumentVector2Map(v2);
            HashSet<String> words = new HashSet<String>();
            words.addAll(m1.keySet());
            words.addAll(m2.keySet());
            //填充向量，添加为权重0的词语
            DocumentVector2MapUtil.fillVector(m1, words);
            DocumentVector2MapUtil.fillVector(m2, words);
            //使用近义词关系更新向量
            updateVectorWithSynonym(m1);
            updateVectorWithSynonym(m2);
            double denominator1 = 0, denominator2 = 0, son = 0;
            for (Map.Entry<String, Double> e : m1.entrySet()) {
                denominator1 += e.getValue() * e.getValue();
                son += e.getValue() * m2.get(e.getKey());  //顺带在此循环内计算结果的分子
            }
            for (Map.Entry<String, Double> e : m2.entrySet()) {
                denominator2 += e.getValue() * e.getValue();
            }
            result = son / Math.sqrt(denominator1 * denominator2);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("计算向量相似度出现问题。");
            return 0;
        }
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

    /*
    使用近义词关系表更新文档向量
     */
    private void updateVectorWithSynonym(Map<String, Double> m1) {
        try {
            Set<String> words = m1.keySet();
            for (Map.Entry<String, Double> e : m1.entrySet()) {
                if (e.getValue() - 0 < 10e-5) { //对于权重为0的向量维度
                    double sim = Double.MIN_VALUE;
                    String syno = null;
                    for (String w : words) {
                        if (m1.get(w) - 0 > 10e-5) {
                            double t = calSynonym(w, e.getKey());
                            if (t > sim) {
                                sim = t;
                                syno = w;
                            }
                        }
                    }
                    if (syno != null) { //更新权重
                        e.setValue(m1.get(syno) * sim);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("使用近义词关系更新向量的时候出现问题");
        }
    }

}
