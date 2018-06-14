package com.charles.na.mapper;

import com.charles.na.model.DocumentVector;

import java.util.List;
import java.util.Map;

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
    DocumentVector findById(long id);

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

    /**
     * @param map
     * @return
     * @description 分页查询
     */
    List<DocumentVector> findByPageInfo(Map<String, Integer> map);

    /**
     * 获取当天所有文本向量
     *
     * @param date
     * @return
     */
    List<DocumentVector> findAllByDate(String date);

    /**
     * @return
     * @description 查询总记录条数
     */
    int queryNum();
}
