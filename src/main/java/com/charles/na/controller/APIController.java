package com.charles.na.controller;

import com.charles.na.service.IEventService;
import com.charles.na.service.IResultService;
import com.charles.na.service.ITrackService;
import com.charles.na.soa.INewsSOAService;
import com.charles.na.utils.SignUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Signature;

/**
 * @author Charles
 * @create 2018/3/30
 * @description api暴露控制层，提供供配置管理模块访问的接口
 * @since 1.0
 */
@Controller
@RequestMapping(value = "/api", name = "提供数据预处理和分析的批量处理接口")
public class APIController {

    private Logger LOGGER = Logger.getLogger(APIController.class);

    @Autowired
    private INewsSOAService newsSOAService;

    @Autowired
    private ITrackService trackService;

    @Autowired
    private IResultService resultService;

    private static String SUCCESS = "SUCCESS";

    private static String FAILURE = "FAILURE";

    @RequestMapping(value = "/vector", name = "创建文本向量")
    public void builVector(HttpServletResponse response) throws IOException {
        try {
            boolean result = newsSOAService.vector();
            if (result) {
                LOGGER.info("文本向量建立成功");
                response.getOutputStream().write(SUCCESS.getBytes("utf-8"));
            } else {
                LOGGER.info("文本向量建立失败");
                response.getOutputStream().write(FAILURE.getBytes("utf-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("文本向量建立过程出错");
            response.getOutputStream().write(e.getMessage().getBytes("utf-8"));
        }
    }

    @RequestMapping(value = "/cluster", name = "聚类")
    public void cluster(HttpServletResponse response) throws IOException {
        try {
            newsSOAService.cluster();
            LOGGER.info("新闻聚类成功");
            response.getOutputStream().write(SUCCESS.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("新闻聚类过程出错");
            response.getOutputStream().write(e.getMessage().getBytes("utf-8"));
        }
    }

    @RequestMapping(value = "/topic", name = "提取话题")
    public void topic(HttpServletResponse response) throws IOException {
        try {
            newsSOAService.topic();
            LOGGER.info("提取话题成功");
            response.getOutputStream().write(SUCCESS.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("提取话题过程出错");
            response.getOutputStream().write(e.getMessage().getBytes("utf-8"));
        }
    }

    @RequestMapping(value = "/event", name = "事件归类")
    public void event(HttpServletResponse response) throws IOException {
        try {
            trackService.track();
            LOGGER.info("事件归类成功");
            response.getOutputStream().write(SUCCESS.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("事件归类过程出错");
            response.getOutputStream().write(e.getMessage().getBytes("utf-8"));
        }
    }

    @RequestMapping(value = "/result", name = "抽取结果")
    public void result(HttpServletResponse response) throws IOException {
        try {
            resultService.convert2result();
            LOGGER.info("抽取结果成功");
            response.getOutputStream().write(SUCCESS.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("抽取结果过程出错");
            response.getOutputStream().write(e.getMessage().getBytes("utf-8"));
        }
    }


}
