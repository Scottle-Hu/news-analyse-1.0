package com.charles.na.service.impl;

import com.charles.na.service.INewsService;
import com.charles.na.service.ITopicService;
import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Word;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * ITopicService实现
 *
 * @author huqj
 * @since 1.0
 */
@Service("topicService")
public class TopicServiceImpl implements ITopicService {

    private Logger LOGGER = Logger.getLogger(TopicServiceImpl.class);

    /**
     * 作为文本关键词的最小出现次数
     */
    private static int MIN_TIMES = 2;

    public List<String> abstractKeywords(String text) {
        List<String> keys = new ArrayList<String>();
        Map<String, List<Integer>> index = new HashMap<String, List<Integer>>();
        Dictionary dict = Dictionary.getInstance();
        MMSeg mmSeg = new MMSeg(new StringReader(text), new ComplexSeg(dict));
        Word word = null;
        List<String> words = new ArrayList<String>();
        try {
            int order = 0;
            //建立 索引位置->单词 的map
            while ((word = mmSeg.next()) != null) {
                String wordStr = word.toString();
                if (NewsServiceImpl.stopWords.contains(wordStr)) {
                    continue;
                }
                words.add(wordStr);
                if (index.get(wordStr) == null) {
                    index.put(wordStr, new ArrayList<Integer>());
                }
                index.get(wordStr).add(order);
                order++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("分词过程出现未知问题");
        }
        //筛选，只留下出现次数大于 最小阈值的单词
        Map<String, Integer> f = new HashMap<String, Integer>();
        for (Map.Entry<String, List<Integer>> e : index.entrySet()) {
            if (e.getValue().size() >= MIN_TIMES) {
                f.put(e.getKey(), e.getValue().size());
            }
        }
        if (f.size() == 0) {
            System.out.println("没有明显关键词");
            return keys;
        }
        //记录上一次
        Map<String, Integer> f2 = f;
        //记录新的
        Map<String, List<Integer>> index2 = new HashMap<String, List<Integer>>();
        //开始循环扩展关键词短语
        int keyLen = 1;
        while (f.size() > 0) {
            index2 = new HashMap<String, List<Integer>>();
            for (Map.Entry<String, Integer> e : f.entrySet()) {
                List<Integer> orders = index.get(e.getKey());
                if (orders == null) {
                    continue;
                }
                for (int o : orders) {
                    if (o == words.size() - keyLen) {  //没有下一个单词
                        continue;
                    }
                    String next = words.get(o + keyLen);
                    String key = e.getKey() + ":" + next;
                    if (index2.get(key) == null) {
                        index2.put(key, new ArrayList<Integer>());
                    }
                    index2.get(key).add(o);
                }
            }
            index = index2;
            f = new HashMap<String, Integer>();
            for (Map.Entry<String, List<Integer>> e : index.entrySet()) {
                if (e.getValue().size() >= MIN_TIMES) {
                    f.put(e.getKey(), e.getValue().size());
                    f2.put(e.getKey(), e.getValue().size());
                }
            }
            keyLen++;
        }
        // System.out.println(f2);
        //排序筛选
        filterKeywords(f2);
        keys.addAll(f2.keySet());
        return keys;
    }

    /**
     * 对提取的关键词短语做排序筛选
     */
    private void filterKeywords(Map<String, Integer> f) {
        Map<List<String>, Integer> f2 = new HashMap<List<String>, Integer>();
        for (Map.Entry<String, Integer> e : f.entrySet()) {
            List<String> key = Arrays.asList(e.getKey().split(":"));
            f2.put(key, e.getValue());
        }
        int maxNum = 0;
        int maxSize = 0;
        Set<List<String>> lists = new HashSet<List<String>>();
        lists.addAll(f2.keySet());
        Iterator<Map.Entry<List<String>, Integer>> iterator = f2.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<List<String>, Integer> e = iterator.next();
            if (e.getValue() > maxNum) {
                maxNum = e.getValue();
            }
            if (e.getKey().size() > maxSize) {
                maxSize = e.getKey().size();
            }
            List<String> key = e.getKey();
            for (List<String> key2 : lists) {
                if (isContains(key, key2)) {  //有包含则只取最长的短语
                    iterator.remove();
                    break;
                }
            }
        }
        f.clear();
        //去除过短的短语
        maxSize = maxSize >> 1;
        Iterator<Map.Entry<List<String>, Integer>> it = f2.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<List<String>, Integer> e = it.next();
            if (e.getKey().size() < maxSize) {
                it.remove();
            }
        }

        //超过两个候选短语则取排名前1个的，有并列则全取
        if (f2.size() > 1) {
            for (int max = maxNum; max > 0; max--) {
                for (Map.Entry<List<String>, Integer> e : f2.entrySet()) {
                    if (e.getValue() == max) {
                        List<String> list = e.getKey();
                        StringBuilder key = new StringBuilder();
                        for (int i = 0; i < list.size(); i++) {
                            key.append(list.get(i));
                        }
                        f.put(key.toString(), e.getValue());
                    }
                }
                if (f.size() >= 1) {
                    break;
                }
            }
        } else {
            for (Map.Entry<List<String>, Integer> e : f2.entrySet()) {
                List<String> list = e.getKey();
                StringBuilder key = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    key.append(list.get(i));
                }
                f.put(key.toString(), e.getValue());
            }
        }
        //TODO 还需要考虑到关键词短语的“纯洁性”

    }

    private boolean isPrefix(List<String> pre, List<String> list) {
        if (list.size() >= pre.size()) {
            return false;
        }
        for (int i = 0; i < pre.size(); i++) {
            if (!pre.get(i).equals(list.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isContains(List<String> sub, List<String> list) {
        if (list.size() <= sub.size()) {
            return false;
        }
        List<String> tmp = new ArrayList<String>();
        tmp.addAll(sub);
        tmp.removeAll(list);
        return tmp.size() == 0;
    }
}
