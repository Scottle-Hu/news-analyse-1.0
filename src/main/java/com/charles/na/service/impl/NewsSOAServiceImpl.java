package com.charles.na.service.impl;

import com.charles.na.mapper.DocumentVectorMapper;
import com.charles.na.mapper.OptRecordMapper;
import com.charles.na.model.DocumentVector;
import com.charles.na.model.News;
import com.charles.na.model.OptRecord;
import com.charles.na.service.INewsSOAService;
import com.charles.na.service.INewsService;
import com.charles.na.utils.IDUtil;
import com.charles.na.utils.TimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * @create 2018/3/28
 * @description 新闻相关分析soa服务实现
 * @since 1.0
 */
@Service("newsSOAService")
public class NewsSOAServiceImpl implements INewsSOAService {

    @Resource
    private INewsService newsService;

    @Resource
    private DocumentVectorMapper documentVectorMapper;

    @Resource
    private OptRecordMapper optRecordMapper;

    private static int PAGE_SIZE = 100;

    public void split() {
        int totalNum = 0;
        try {
            long start = System.currentTimeMillis();
            Map<String, Integer> pageInfo = new HashMap<String, Integer>();
            pageInfo.put("pageNo", 0);
            pageInfo.put("pageSize", PAGE_SIZE);
            List<News> newsList = newsService.findByPage(pageInfo);
            while (newsList != null && newsList.size() > 0) {
                for (News news : newsList) {
                    totalNum += 1; //记录
                    DocumentVector dv = new DocumentVector();
                    dv.setId(IDUtil.generateID());
                    dv.setNewsId(news.getId());
                    Map<String, Integer> map = newsService.splitById(news.getId());
                    String vector = "(";
                    for (Map.Entry<String, Integer> e : map.entrySet()) {
                        vector = vector + e.getKey() + ":" + e.getValue() + " ";
                    }
                    vector = vector.substring(0, vector.length() - 1) + ")";
                    dv.setVector(vector);
                    dv.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    //插入文本向量
                    documentVectorMapper.insert(dv);
                }
                pageInfo.put("pageNo", pageInfo.get("pageNo") + PAGE_SIZE);
                newsList = newsService.findByPage(pageInfo);
            }
            long end = System.currentTimeMillis();
            System.out.println("本次文档向量建立过程耗费时间：" + TimeUtil.convertMillis2String(end - start));
            //将操作成功的记录写入数据库
            OptRecord optRecord = new OptRecord();
            optRecord.setId(IDUtil.generateID());
            optRecord.setOpt("建立文档向量模型");
            optRecord.setOptNum(totalNum);
            optRecord.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            optRecord.setStatus(1);
            optRecordMapper.insert(optRecord);
        } catch (Exception e) {
            e.printStackTrace();
            //将操作失败的记录写入数据库
            OptRecord optRecord = new OptRecord();
            optRecord.setId(IDUtil.generateID());
            optRecord.setOpt("建立文档向量模型");
            optRecord.setOptNum(totalNum);
            optRecord.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            optRecord.setStatus(0);
            optRecordMapper.insert(optRecord);
        }
    }

    public void cluster() {
        //TODO 文本聚类实现

    }
}
