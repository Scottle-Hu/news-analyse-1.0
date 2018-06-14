package com.charles.na.service.impl;

import com.charles.na.mapper.NewsMapper;
import com.charles.na.mapper.StopWordMapper;
import com.charles.na.model.News;
import com.charles.na.model.Words;
import com.charles.na.service.INewsService;
import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Word;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.ws.ServiceMode;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.charles.na.common.Constant.TITLE_WEIGHT;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 新闻实体相关业务封装实现
 * @since 1.0
 */
@Service("newsService")
public class NewsServiceImpl implements INewsService {

    @Resource
    private NewsMapper newsMapper;

    @Resource
    private StopWordMapper stopWordMapper;

    public static HashSet<String> stopWords = new HashSet<String>();

    @PostConstruct
    public void loadStopWords() {
        System.out.println("=====开始加载停用词表到内存====");
        //加载停用词表，避免反复读取数据库
        List<Words> stopWordList = stopWordMapper.findAll();
        for (Words w : stopWordList) {
            stopWords.add(w.getWord());
        }
        System.out.println("=====结束加载停用词表到内存====");
    }

    public Map<String, Integer> splitById(String id) throws IOException {
        Map<String, Integer> result = new HashMap<String, Integer>();
        News news = newsMapper.findById(id);
        String content = news.getContent();
        String title = news.getTitle();
        //标题出现的词赋予不同权重
        for (int i = 0; i < TITLE_WEIGHT; i++) {
            content = content + " " + title;
        }
        //mmseg4j分词
        Dictionary dict = Dictionary.getInstance();
        MMSeg mmSeg = new MMSeg(new StringReader(content), new ComplexSeg(dict));
        Word word = null;
        while ((word = mmSeg.next()) != null) {
            String wordStr = word.toString();
            if (stopWords.contains(wordStr)) { //去除停用词
                continue;
            }
            if (result.get(wordStr) == null) {
                result.put(wordStr, 1);
            } else {
                result.put(wordStr, result.get(wordStr) + 1);
            }
        }
        System.out.println("文章" + id + " 文档向量创建完毕");
        return result;
    }

    public synchronized List<News> findByPage(int offset, int limit, String date) {
        return newsMapper.findByPage(offset, limit, date);
    }

    public int queryNum(String date) {
        return newsMapper.queryNum(date);
    }

    public News findById(String id) {
        return newsMapper.findById(id);
    }

}
