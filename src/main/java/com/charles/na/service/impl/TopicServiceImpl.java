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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, Integer> f2 = null;
        //记录新的
        Map<String, List<Integer>> index2 = new HashMap<String, List<Integer>>();
        //开始循环扩展关键词短语
        int keyLen = 1;
        while (f.size() > 0) {
            f2 = f;
            index2 = new HashMap<String, List<Integer>>();
            for (Map.Entry<String, Integer> e : f.entrySet()) {
                List<Integer> orders = index.get(e.getKey());
                for (int o : orders) {
                    if (o == words.size() - keyLen) {  //没有下一个单词
                        continue;
                    }
                    String next = words.get(o + keyLen);
                    String key = e.getKey() + "|" + next;
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
                }
            }
            keyLen++;
        }
        System.out.println(f2);
        for (Map.Entry<String, Integer> e : f2.entrySet()) {
            String res = e.getKey().replace("|", "");
            keys.add(res);
        }
        return keys;
    }
}
