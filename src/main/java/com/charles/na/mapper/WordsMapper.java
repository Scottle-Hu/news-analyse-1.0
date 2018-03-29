package com.charles.na.mapper;

import com.charles.na.model.Words;

import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * @create 2018/3/28
 * @description t_words表数据库操作接口
 * @since 1.0
 */
public interface WordsMapper {

    /**
     * @param id
     * @return
     * @description 根据id查询
     */
    Words findById(String id);

    /**
     * @return
     * @description 查询所有记录
     */
    List<Words> findAll();

    /**
     * @return
     * @description 插入一条记录
     */
    int insert(Words words);

    /**
     * @param words
     * @return
     * @description 修改一条记录
     */
    int update(Words words);

    /**
     * @param id
     * @return
     * @description 删除一条记录
     */
    int deleteById(String id);
}
