package com.charles.na.controller;

import com.charles.na.mapper.ClusterMapper;
import com.charles.na.model.Cluster;
import com.charles.na.model.News;
import com.charles.na.service.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * 用于展示各种调试信息
 *
 * @author huqj
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private INewsService newsService;

    @Autowired
    private ClusterMapper clusterMapper;

    @RequestMapping("/cluster")
    public String cluster(HttpServletRequest request, HttpServletResponse response) {
        String date = new SimpleDateFormat("yy-MM-dd").format(new Date());
        List<Cluster> clusters = clusterMapper.findByDate(date);
        Map<String, List<News>> show = new HashMap<String, List<News>>();
        for (Cluster c : clusters) {
            if (show.get(c.getId()) == null) {
                show.put(c.getId(), new ArrayList<News>());
            }
            show.get(c.getId()).add(newsService.findById(c.getNewsId()));
        }
        request.setAttribute("cluster", show);
        return "cluster";
    }
    
}
