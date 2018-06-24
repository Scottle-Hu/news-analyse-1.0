package com.charles.na;

import com.charles.na.service.IMainService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 分析系统的执行入口，每天定时执行
 *
 * @author huqj
 */
public class AnalyseApplication {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext("/applicationContext.xml");
        IMainService mainService = (IMainService) ctx.getBean("mainService");
        mainService.analyse();
    }
}
