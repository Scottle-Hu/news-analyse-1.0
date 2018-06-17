package com.charles.na.web.service;

import com.charles.na.web.model.User;

/**
 * 用户登录、注册、订阅事件的业务处理
 *
 * @author huqj
 */
public interface IUserService {

    String login(User user);

    boolean isLogin(User user);

    void updateLogin(User user);

}
