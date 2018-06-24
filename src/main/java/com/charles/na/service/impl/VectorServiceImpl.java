package com.charles.na.service.impl;

import com.charles.na.mapper.SynonymMapper;
import com.charles.na.model.DocumentVector;
import com.charles.na.model.Synonym;
import com.charles.na.service.IVectorService;
import com.charles.na.soa.impl.thread.BuildDocumentVectorThread;
import com.charles.na.utils.DocumentVector2MapUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * @author Charles
 * @create 2018/3/29
 * @description 向量服务类
 * @since 1.0
 */
@Service("vectorService")
public class VectorServiceImpl implements IVectorService {

    private Logger LOGGER = Logger.getLogger(VectorServiceImpl.class);

    /**
     * 词语 -> 编码 的映射关系
     */
    private Map<String, String> synonymMap = new HashMap<String, String>();

    private static final int PAGE_SIZE = 500;

    @Resource
    private SynonymMapper synonymMapper;

    /**
     * 加载近义词表到内存中
     */
    @PostConstruct
    public void getSynonym() {
        System.out.println("============开始加载近义词表==========");
        Map<String, Integer> pageInfo = new HashMap<String, Integer>();
        pageInfo.put("pageNo", 0);
        pageInfo.put("pageSize", PAGE_SIZE);
        List<Synonym> synonymList = synonymMapper.findByPageInfo(pageInfo);
        while (synonymList != null && synonymList.size() > 0) {
            for (Synonym s : synonymList) {
                synonymMap.put(s.getWord(), s.getCode());
            }
            pageInfo.put("pageNo", pageInfo.get("pageNo") + PAGE_SIZE);
            synonymList = synonymMapper.findByPageInfo(pageInfo);
        }
        System.out.println("============结束加载近义词表==========");
    }

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
     * @param m1
     * @param m2
     * @return
     * @description 计算两个文本向量之间的相似度
     */
    public double calSimilarityBetweenMap(Map<String, Double> m1, Map<String, Double> m2) {
        try {
            double result = 0;
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
     * canopy阶段计算文档相似度的简单方法
     *
     * @param v1
     * @param v2
     * @return
     */
    public double simpleCalSimilarity(DocumentVector v1, DocumentVector v2) {
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
            int num = 0;
            for (Map.Entry<String, Double> e : m1.entrySet()) {
                double x1 = e.getValue();
                double x2 = m2.get(e.getKey());
                result += Math.abs(x1 - x2) / (x1 + x2);
                num++;
            }
            return result / num;
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
        String code1 = synonymMap.get(word1);
        String code2 = synonymMap.get(word2);
        if (code1 != null && code2 != null) {
            if (code1.endsWith("@") || code2.endsWith("@")) {  //'@'代表自封闭的词语
                return result;
            }
            if (code1.equals(code2)) {
                if (code1.endsWith("=")) {  //相同含义
                    return 1;
                } else {  //相关含义
                    return 0.9;
                }
            }
            for (int i = 5; i > 0; i--) {  //根据相同编码个数计算相似度
                if (code1.substring(0, i).equals(code2.substring(0, i))) {
                    result = (i / 5) * 0.9;
                    return result;
                }
            }
        }
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
