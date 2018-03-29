package com.charles.na.mapper;

import com.charles.na.model.DocumentVector;

import java.util.List;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 文本向量预处理结果数据库操作
 * @since 1.0
 */
public interface DocumentVectorMapper {

    /**
     * @param id
     * @return
     * @description 根据id查询文本向量
     */
    DocumentVector findById(String id);

    /**
     * @param newsId
     * @return
     * @description 根据文章id查询文档向量
     */
    List<DocumentVector> findByNewsId(String newsId);

    /**
     * @param dv
     * @return
     * @description 插入记录
     */
    int insert(DocumentVector dv);
}
