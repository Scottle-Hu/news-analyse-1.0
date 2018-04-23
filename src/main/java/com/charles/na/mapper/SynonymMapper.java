package com.charles.na.mapper;

import com.charles.na.model.DocumentVector;
import com.charles.na.model.Synonym;
import com.sun.javafx.collections.MappingChange;

import java.util.Map;

import java.util.List;

/**
 * @author Charles
 * @create 2018/3/29
 * @description 同义词数据库操作
 * @since 1.0
 */
public interface SynonymMapper {

    /**
     * @param id
     * @return
     * @description 根据id查询词条记录
     */
    Synonym findById(String id);

    /**
     * @param word
     * @return
     * @description 根据单词查找同义词
     */
    Synonym findByWord(String word);

    /**
     * @return
     * @description 根据分页查询所有近义词
     */
    List<Synonym> findByPageInfo(Map<String, Integer> map);
}
