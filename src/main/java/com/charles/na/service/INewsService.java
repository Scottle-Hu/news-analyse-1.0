package com.charles.na.service;

import com.charles.na.model.News;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 新闻实体相关业务封装接口
 * @since 1.0
 */
public interface INewsService {

    /**
     * @param id
     * @return
     * @description 按照新闻id进行单词计数
     */
    Map<String, Integer> splitById(String id) throws IOException;

    /**
     * @param map 包含两个参数：pageNo,pageSize
     * @return
     * @description 分页查询
     */
    List<News> findByPage(int offset, int limit, String date);

    /**
     * @return
     * @description 查询新闻条数
     */
    int queryNum(String date);

    /**
     * @param id
     * @return
     * @description 根据id查询
     */
    News findById(String id);

}
