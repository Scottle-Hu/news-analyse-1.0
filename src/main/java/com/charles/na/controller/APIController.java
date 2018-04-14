package com.charles.na.controller;

import com.charles.na.soa.INewsSOAService;
import com.charles.na.utils.SignUtil;
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

    @Autowired
    private INewsSOAService newsSOAService;

    private static String SUCCESS = "SUCCESS";

    private static String FAILURE = "FAILURE";

    @RequestMapping(value = "/buildvector", name = "创建文本向量")
    public void builVector(@RequestParam("token") String token,
                           @RequestParam("sign") String sign, HttpServletResponse response) throws IOException {
        if (!SignUtil.generateSign(token).equals(sign)) {
            response.getOutputStream().write(FAILURE.getBytes("utf-8"));
        }
        boolean result = newsSOAService.vector();
        if (result) {
            response.getOutputStream().write(SUCCESS.getBytes("utf-8"));
        } else {
            response.getOutputStream().write(FAILURE.getBytes("utf-8"));
        }
    }


}
