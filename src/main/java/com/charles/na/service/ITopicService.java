package com.charles.na.service;

import java.util.List;

/**
 * 使用频繁短语发现算法抽取文本中的关键词
 *
 * @author huqj
 * @since 1.0
 */
public interface ITopicService {

    /**
     * @param text 待抽取关键词的文本
     * @return 关键词短语
     */
    List<String> abstractKeywords(String text);

}
