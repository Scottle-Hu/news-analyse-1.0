package com.charles.na.web.controller;

import com.charles.na.model.Event;
import com.charles.na.model.EventResult;
import com.charles.na.model.News;
import com.charles.na.web.model.MainPageInfo;
import com.charles.na.web.model.NameAndValue;
import com.charles.na.web.model.User;
import com.charles.na.web.service.IQueryService;
import com.charles.na.web.service.ISearchService;
import com.charles.na.web.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * web展示系统的controller，提供各种接口
 *
 * @author huqj
 */
@Controller
@RequestMapping("/web")
public class MainController {

    private Logger LOGGER = Logger.getLogger(MainController.class);

    @Autowired
    private IQueryService queryService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISearchService searchService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/query")
    public void queryByDuring(@RequestParam(value = "start", required = false) String start,
                              @RequestParam(value = "end", required = false) String end,
                              @RequestParam(value = "token", required = false) String token,
                              HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        Map<String, Object> result = new HashMap<String, Object>();
        //校验用户token
        if (StringUtils.isBlank(token)) {
            result.put("code", 1);
            result.put("msg", "用户验证错误。");
        } else {
            User user = checkUserLoginStatus(token);
            if (user == null) {
                result.put("code", 1);
                result.put("msg", "用户验证错误。");
            } else {
                result.put("username", user.getUsername());
                if (end == null || start == null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    end = sdf.format(new Date());
                    start = sdf.format(new Date(System.currentTimeMillis() - 7 * 24 * 3600 * 1000));  //默认最近一周
                }
                LOGGER.info("开始筛选热点：" + start + " - " + end);
                MainPageInfo mainPageInfo = queryService.queryByDuring(start, end);
                if (mainPageInfo == null) {
                    result.put("code", 1);
                    result.put("msg", "没有查询到数据");
                    //没有数据也要填充
                    mainPageInfo = new MainPageInfo();
                    mainPageInfo.setStartDate(start);
                    mainPageInfo.setEndDate(end);
                    mainPageInfo.setHotWords(Collections.<NameAndValue>emptyList());
                    mainPageInfo.setHotPeople(Collections.<NameAndValue>emptyList());
                    mainPageInfo.setNegativeEvent(Collections.<NameAndValue>emptyList());
                    mainPageInfo.setTopEvent(Collections.<EventResult>emptyList());
                } else {
                    result.put("code", 0);
                }
                result.put("info", mainPageInfo);
            }
        }
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(objectMapper.writeValueAsString(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("/event")
    public void event(@RequestParam(value = "id", required = false) String id,
                      @RequestParam(value = "end", required = false) String end,
                      @RequestParam(value = "token", required = false) String token,
                      HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        Map<String, Object> result = new HashMap<String, Object>();
        //校验用户token
        if (StringUtils.isBlank(token)) {
            result.put("code", 1);
            result.put("msg", "用户验证错误。");
        } else {
            User user = checkUserLoginStatus(token);
            if (user == null) {
                result.put("code", 1);
                result.put("msg", "用户验证错误。");
            } else {
                result.put("username", user.getUsername());
                if (StringUtils.isBlank(end) || StringUtils.isBlank(id)) {
                    result.put("code", 1);
                    result.put("msg", "参数为空");
                } else {
                    LOGGER.info("开始查询热点：" + id + " - " + end);
                    EventResult event = queryService.findEventResultByDateAndId(end, id);
                    if (event == null) {
                        event = new EventResult();
                        result.put("code", 1);
                        result.put("msg", "没有查询到记录");
                        //没有数据也要填充
                        event.setCurHot(0);
                        event.setPeopleMap(Collections.<NameAndValue>emptyList());
                        event.setSentimentMap(Collections.<NameAndValue>emptyList());
                        event.setNewsList(Collections.<News>emptyList());
                        event.setTitle(null);
                        event.setHotTend(Collections.<String, List>emptyMap());
                    } else {
                        result.put("code", 0);
                    }
                    result.put("event", event);
                }
            }
        }
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(objectMapper.writeValueAsString(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("/search")
    public void event(@RequestParam(value = "key", required = false) String key,
                      @RequestParam(value = "token", required = false) String token,
                      HttpServletResponse response, HttpServletRequest request) throws UnsupportedEncodingException {
        key = new String(key.getBytes("iso-8859-1"), "utf-8");//中文参数乱码
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        Map<String, Object> result = new HashMap<String, Object>();
        //校验用户token
        if (StringUtils.isBlank(token)) {
            result.put("code", 1);
            result.put("msg", "用户验证错误。");
        } else {
            User user = checkUserLoginStatus(token);
            if (user == null) {
                result.put("code", 1);
                result.put("msg", "用户验证错误。");
            } else {
                result.put("username", user.getUsername());
                if (StringUtils.isBlank(key)) {
                    result.put("code", 1);
                    result.put("msg", "参数错误。");
                } else {
                    LOGGER.info("开始搜索热点：" + key);
                    List<Event> eventList = searchService.searchEvent(key);
                    if (CollectionUtils.isEmpty(eventList)) {
                        LOGGER.info("未搜索到相关热点");
                        result.put("code", 1);
                        result.put("msg", "没有搜索到相关话题");
                        eventList = Collections.emptyList();
                    } else {
                        result.put("code", 0);
                    }
                    result.put("result", eventList);
                }
            }
        }
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(objectMapper.writeValueAsString(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("/login")
    public void login(@RequestParam(value = "username", required = false) String username,
                      @RequestParam(value = "password", required = false) String password,
                      HttpServletResponse response, HttpServletRequest request) throws UnsupportedEncodingException {
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            result.put("code", 1);
            result.put("msg", "用户名或密码缺失");
        } else {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            String login = userService.login(user);
            if (login == null) {
                result.put("code", 1);
                result.put("msg", "用户名和密码错误");
            } else {
                result.put("code", 0);
                result.put("token", login);
            }
        }
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(objectMapper.writeValueAsString(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("/logout")
    public void logout(@RequestParam(value = "token", required = false) String token,
                       HttpServletResponse response) throws UnsupportedEncodingException {
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtils.isBlank(token)) {
            result.put("code", 1);
            result.put("msg", "token缺失");
        } else {
            boolean success = userService.logout(token);
            if (success) {
                result.put("code", 0);
            } else {
                result.put("code", 1);
                result.put("msg", "token不存在");
                LOGGER.info("用户退出失败，token:" + token);
            }
        }
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(objectMapper.writeValueAsString(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //根据用户查询订阅事件
    @RequestMapping("/getsubscribe")
    public void subscribe(@RequestParam(value = "token", required = false) String token,
                          HttpServletResponse response) throws UnsupportedEncodingException {
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtils.isBlank(token)) {
            result.put("code", 1);
            result.put("msg", "token缺失");
        } else {
            User user = checkUserLoginStatus(token);
            if (user == null) {
                result.put("code", 1);
                result.put("msg", "用户验证错误。");
            } else {
                result.put("code", 0);
                List<Event> eventList = userService.findSubscribeEvent(user);
                if (CollectionUtils.isEmpty(eventList)) {
                    LOGGER.info("该用户未订阅任何事件:" + token);
                }
                result.put("info", eventList);
            }
        }
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(objectMapper.writeValueAsString(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //用户订阅事件
    @RequestMapping("/subscribe")
    public void subscribe(@RequestParam(value = "token", required = false) String token,
                          @RequestParam(value = "eventId", required = false) String eventId,
                          HttpServletResponse response) throws UnsupportedEncodingException {
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtils.isBlank(token)) {
            result.put("code", 1);
            result.put("msg", "token缺失");
        } else {
            User user = checkUserLoginStatus(token);
            if (user == null) {
                result.put("code", 1);
                result.put("msg", "用户验证错误。");
            } else {
                boolean success = userService.subscribeEvent(user.getId(), eventId);
                if (success) {
                    result.put("code", 0);
                } else {
                    result.put("code", 1);
                    result.put("msg", "订阅失败，请稍候再试");
                }
            }
        }
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(objectMapper.writeValueAsString(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //检查并且更新
    private User checkUserLoginStatus(String token) {
        User user = userService.isLogin(token);
        userService.updateLogin(user);
        return user;
    }


}
