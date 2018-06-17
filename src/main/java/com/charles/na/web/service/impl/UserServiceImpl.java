package com.charles.na.web.service.impl;

import com.charles.na.mapper.UserMapper;
import com.charles.na.utils.RedisUtil;
import com.charles.na.web.model.User;
import com.charles.na.web.service.IUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


/**
 * @author huqj
 */
@Service("userService")
public class UserServiceImpl implements IUserService {

    private Logger LOGGER = Logger.getLogger(UserServiceImpl.class);

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.pass}")
    private String redisPass;

    private RedisUtil redisUtil;

    //用户登录token的过期时间，三十分钟
    private int expireSec = 30 * 60;
    @Autowired
    private UserMapper userMapper;

    @PostConstruct
    public void init() {
        //初始化redis操作工具
        redisUtil = new RedisUtil(redisHost, redisPort, redisPass);
    }

    public String login(User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return null;
        }
        List<User> check = userMapper.findByUsernameAndPassword(user);
        if (CollectionUtils.isEmpty(check) || check.size() > 1) {  //验证失败
            return null;
        } else {  //验证成功
            //计算token并存入redis，设置过期时间
            String message = user.getUsername() + "#" + user.getPassword();
            String digest = md5(message);
            String key = "token:" + digest;
            redisUtil.set(key, "yes");
            redisUtil.setExpire(key, expireSec);
            return digest;
        }
    }

    /**
     * 判断用户是否已经登录
     *
     * @param user
     * @return
     */
    public boolean isLogin(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return false;
        }
        String digest = md5(user.getUsername() + "#" + user.getPassword());
        String key = "token:" + digest;
        if (redisUtil.get(key) == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 更新用户的登录会话过期时间
     *
     * @param user
     */
    public void updateLogin(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return;
        }
        String digest = md5(user.getUsername() + "#" + user.getPassword());
        String key = "token:" + digest;
        redisUtil.set(key, "yes");
        redisUtil.setExpire(key, expireSec);
    }

    /**
     * md5加密算法
     *
     * @param str
     * @return
     */
    private String md5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        BASE64Encoder base64en = new BASE64Encoder();
        String newstr = null;
        try {
            newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return newstr;
    }

}
