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
        if (args.length < 1) {
            System.out.println("参数不足！！默认开始all分析流程");
            args = new String[1];
            args[0] = "all";
        }
        String service = args[0];
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext("/applicationContext.xml");
        IMainService mainService = (IMainService) ctx.getBean("mainService");
        if (args.length == 2) {
            String date = args[1];
            mainService.setDate(date);
            System.out.println("自定义分析日期：" + date);
        }
        if (service.equals("all")) {
            System.out.println("开始全部分析流程...");
            mainService.analyse();
        } else if (service.equals("news")) {
            System.out.println("开始news分析流程...");
            mainService.getNewsSOAService().vector();
            mainService.getNewsSOAService().cluster();
            mainService.getNewsSOAService().topic();
        } else if (service.equals("vector")) {
            System.out.println("开始vector分析流程...");
            mainService.getNewsSOAService().vector();
        } else if (service.equals("cluster")) {
            System.out.println("开始cluster分析流程...");
            mainService.getNewsSOAService().cluster();
        } else if (service.equals("topic")) {
            System.out.println("开始topic分析流程...");
            mainService.getNewsSOAService().topic();
        } else if (service.equals("track")) {
            System.out.println("开始track分析流程...");
            mainService.getTrackService().track();
        } else if (service.equals("result")) {
            System.out.println("开始result分析流程...");
            mainService.getResultService().convert2result();
        } else {
            System.out.println("未找到对应的执行模块!!");
        }

    }
}
