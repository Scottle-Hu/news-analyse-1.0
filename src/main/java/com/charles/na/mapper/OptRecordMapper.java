package com.charles.na.mapper;

import com.charles.na.model.DocumentVector;
import com.charles.na.model.OptRecord;

import java.util.List;

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
    DocumentVector findById(String id);

    /**
     * @param or
     * @return
     * @description 插入记录
     */
    int insert(OptRecord or);
}
