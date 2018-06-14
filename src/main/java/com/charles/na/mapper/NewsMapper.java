package com.charles.na.mapper;

import com.charles.na.model.News;

import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 新闻表数据库操作接口
 * @since 1.0
 */
public interface NewsMapper {

    /**
     * @param id
     * @return
     * @description 根据id查询
     */
    News findById(String id);

    /**
     * @param map 包含两个参数：pageNo,pageSize
     * @return
     * @description 分页查询
     */
    List<News> findByPage(int offset, int limit, String date);

    /**
     * @return
     * @description 查询所有记录
     */
    List<News> findAll();

    /**
     * @return
     * @description 插入一条记录
     */
    int insert(News news);

    /**
     * @param news
     * @return
     * @description 修改一条记录
     */
    int update(News news);

    /**
     * @param id
     * @return
     * @description 删除一条记录
     */
    int deleteById(String id);

    /**
     * @return
     * @description 查询新闻条数
     */
    int queryNum(String date);

}
