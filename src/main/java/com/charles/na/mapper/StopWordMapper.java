package com.charles.na.mapper;

import com.charles.na.model.Words;

import java.util.List;

/**
 * @author Charles
 * @create 2018/3/28
 * @description t_words表数据库操作接口
 * @since 1.0
 */
public interface StopWordMapper {
    /**
     * @return
     * @description 查询所有停用词
     */
    List<Words> findAll();
}
