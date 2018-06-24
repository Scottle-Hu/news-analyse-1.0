package com.charles.na.mapper;


import com.charles.na.web.model.User;

import java.util.*;

/**
 * @author huqj
 */
public interface UserMapper {

    /**
     * 根据用户名和密码查询用户表
     *
     * @param user
     * @return
     */
    List<User> findByUsernameAndPassword(User user);

    /**
     * 插入一条用户记录
     *
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * @param id
     * @return
     */
    User findById(String id);


}
