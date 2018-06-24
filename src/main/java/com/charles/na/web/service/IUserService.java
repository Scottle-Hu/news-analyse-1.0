package com.charles.na.web.service;

import com.charles.na.model.Event;
import com.charles.na.web.model.User;

import java.util.List;

/**
 * 用户登录、注册、订阅事件的业务处理
 *
 * @author huqj
 */
public interface IUserService {

    String login(User user);

    boolean logout(String token);

    User isLogin(String token);

    void updateLogin(User user);

    List<Event> findSubscribeEvent(User user);

    boolean subscribeEvent(String userId, String eventId);

}
