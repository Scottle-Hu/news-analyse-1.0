package com.charles.na.utils;

import com.charles.na.model.DocumentVector;

import java.util.*;

/**
 * @author huqj
 * @create 2018/4/17
 * @description 将文档向量转化为map的工具
 */
public class DocumentVector2MapUtil {

    public static Map<String, Double> convertDocumentVector2Map(DocumentVector dv) {
        Map<String, Double> res = new HashMap<String, Double>();
        String vector = dv.getVector();
        vector = vector.substring(1, vector.length() - 1);
        String[] elements = vector.split(" ");
        for (String ele : elements) {
            String[] keyValue = ele.split(":");
            res.put(keyValue[0], Double.parseDouble(keyValue[1]));
        }
        return res;
    }

    public static DocumentVector convertMap2DocumentVector(Map<String, Double> map) {
        DocumentVector dv = new DocumentVector();
        StringBuilder vector = new StringBuilder("(");
        for (Map.Entry<String, Double> e : map.entrySet()) {
            if (Math.abs(e.getValue() - 0) > 10e-6) {
                vector.append(e.getKey() + ":" + e.getValue() + " ");
            }
        }
        if (vector.length() > 1) {
            vector = new StringBuilder(vector.subSequence(0, vector.length() - 1));
        }
        vector.append(")");
        dv.setVector(vector.toString());
        dv.setId(IDUtil.generateID());
        return dv;
    }

    /**
     * @param map
     * @param words
     * @description 使用全局的所有词填充文本向量
     */
    public static void fillVector(Map<String, Double> map, HashSet<String> words) {
        for (String w : words) {
            if (map.get(w) == null) {
                map.put(w, 0.0);
            }
        }
    }

    /**
     * @param set
     * @return
     * @description 计算一组map的聚簇中心map
     */
    public static Map<String, Double> calCenterOfVectors(Set<Map<String, Double>> set) {
        Map<String, Double> res = new HashMap<String, Double>();
        for (Map<String, Double> map : set) {
            for (Map.Entry<String, Double> e : map.entrySet()) {
                if (res.get(e.getKey()) == null) {
                    res.put(e.getKey(), e.getValue());
                } else {
                    res.put(e.getKey(), res.get(e.getKey()) + e.getValue());
                }
            }
        }
        int size = set.size();
        for (Map.Entry<String, Double> e : res.entrySet()) {
            e.setValue(e.getValue() / size);
        }
        return res;
    }

}
