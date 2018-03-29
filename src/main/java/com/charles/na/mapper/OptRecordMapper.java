package com.charles.na.mapper;

import com.charles.na.model.DocumentVector;
import com.charles.na.model.OptRecord;

import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * @create 2018/3/29
 * @description 操作记录数据库操作
 * @since 1.0
 */
public interface OptRecordMapper {

    /**
     * @param id
     * @return
     * @description 根据id查询
     */
    OptRecord findById(String id);

    /**
     * @return
     * @description 查询所有操作记录
     */
    List<OptRecord> findAll();

    /**
     * @param map
     * @return
     * @description 分页查询
     */
    List<OptRecord> findByPage(Map<String, Integer> map);

    /**
     * @param or
     * @return
     * @description 插入记录
     */
    int insert(OptRecord or);
}
