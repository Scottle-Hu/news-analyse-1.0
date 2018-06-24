package com.charles.na.web.service.impl;

import com.charles.na.mapper.EventMapper;
import com.charles.na.mapper.SubscribeMapper;
import com.charles.na.mapper.UserMapper;
import com.charles.na.model.Event;
import com.charles.na.utils.RedisUtil;
import com.charles.na.web.model.Subscribe;
import com.charles.na.web.model.User;
import com.charles.na.web.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;


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

    @Autowired
    private SubscribeMapper subscribeMapper;

    @Autowired
    private EventMapper eventMapper;

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
            redisUtil.set2(key, check.get(0).getId());
            redisUtil.setExpire2(key, expireSec);
            return digest;
        }
    }

    public boolean logout(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        String key = "token:" + token;
        if (redisUtil.get2(key) == null) {
            return false;
        }
        redisUtil.del2(key);
        return true;
    }

    /**
     * 判断用户是否已经登录
     *
     * @param token
     * @return
     */
    public User isLogin(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        String key = "token:" + token;
        String value = redisUtil.get2(key);
        if (value == null) {
            return null;
        } else {
            return userMapper.findById(value);
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
        redisUtil.set2(key, user.getId());
        redisUtil.setExpire2(key, expireSec);
    }

    public List<Event> findSubscribeEvent(User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        if (StringUtils.isEmpty(user.getId())) {
            return Collections.emptyList();
        }
        List<Subscribe> subscribes = subscribeMapper.findEventsIdByUserId(user.getId());
        if (CollectionUtils.isEmpty(subscribes)) {
            return Collections.emptyList();
        }
        List<Event> eventList = new ArrayList<Event>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Subscribe s : subscribes) {
            Event e = eventMapper.findById(s.getEventId());
            if (e != null) {
                e.setPeople(s.getDate());  //people字段携带关注日期过去
                eventList.add(e);
                String curHot = "0";
                String lastUpdate = "";
                String tend = "-";
                try {
                    LinkedHashMap<String, Integer> map = objectMapper.readValue(e.getHot(), LinkedHashMap.class);
                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                        if (!lastUpdate.equals("")) {
                            int hotNum = Integer.parseInt(curHot);
                            if (entry.getValue() > hotNum) {
                                tend = "↑";
                            } else if (entry.getValue() < hotNum) {
                                tend = "↓";
                            } else {
                                tend = "-";
                            }
                        }
                        lastUpdate = entry.getKey();
                        curHot = String.valueOf(entry.getValue() * 2);
                    }
                    e.setHot(curHot + " " + tend); //hot字段携带最后热度和趋势
                    e.setSentiment(lastUpdate); //sentiment字段携带更新日期过去
                } catch (IOException e1) {
                    e1.printStackTrace();
                    LOGGER.error("反解析热度出错。");
                }
            }
        }
        if (CollectionUtils.isEmpty(eventList)) {
            return Collections.emptyList();
        }
        return eventList;
    }

    public boolean subscribeEvent(String userId, String eventId) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(eventId)) {
            return false;
        }
        Subscribe subscribe = new Subscribe();
        subscribe.setUserId(userId);
        subscribe.setEventId(eventId);
        subscribe.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        try {
            subscribeMapper.insertOne(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("插入事件订阅出错");
            return false;
        }
        return true;
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
        String res = "";
        for (int i = 0; i < newstr.length(); i++) {
            char c = newstr.charAt(i);
            if ((c <= 9) || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                res += c;
            }
        }
        return res;
    }

}
