package com.charles.na.soa.impl;

import com.charles.na.common.Constant;
import com.charles.na.mapper.DocumentVectorMapper;
import com.charles.na.mapper.OptRecordMapper;
import com.charles.na.model.DocumentVector;
import com.charles.na.model.News;
import com.charles.na.model.OptRecord;
import com.charles.na.soa.INewsSOAService;
import com.charles.na.service.INewsService;
import com.charles.na.utils.IDUtil;
import com.charles.na.utils.TimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

    private Logger LOGGER = Logger.getLogger(NewsSOAServiceImpl.class);

    @Resource
    private INewsService newsService;

    @Resource
    private DocumentVectorMapper documentVectorMapper;

    @Resource
    private OptRecordMapper optRecordMapper;

    private static int PAGE_SIZE = 50;

    public void vector() {
        int totalNum = 0;
        try {
            long start = System.currentTimeMillis();
            Map<String, Integer> pageInfo = new HashMap<String, Integer>();
            pageInfo.put("pageNo", 0);
            pageInfo.put("pageSize", PAGE_SIZE);
            List<News> newsList = null;
            newsList = queryDBToGetNews(pageInfo);
            while (newsList != null && newsList.size() > 0) {
                for (News news : newsList) {
                    try {
                        totalNum += 1; //记录
                        DocumentVector dv = new DocumentVector();
                        dv.setId(IDUtil.generateID());
                        dv.setNewsId(news.getId());
                        Map<String, Integer> map = newsService.splitById(news.getId());
                        String vector = "(";
                        for (Map.Entry<String, Integer> e : map.entrySet()) {
                            vector = vector + e.getKey() + ":" + e.getValue() + " ";
                        }
                        vector = vector.substring(0, vector.length() - (vector.length() > 1 ? 1 : 0)) + ")";
                        dv.setVector(vector);
                        dv.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        //插入文本向量，同样使用失败重连机制
                        queryDBToInsertVector(dv);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        LOGGER.error("在创建文本" + news.getId() + "时出现问题");
                    }
                }
                pageInfo.put("pageNo", pageInfo.get("pageNo") + PAGE_SIZE);
                newsList = queryDBToGetNews(pageInfo);
            }
            long end = System.currentTimeMillis();
            LOGGER.info("本次文档向量建立过程耗费时间：" + TimeUtil.convertMillis2String(end - start));
            //将操作成功的记录写入数据库
            OptRecord optRecord = new OptRecord();
            optRecord.setId(IDUtil.generateID());
            optRecord.setOpt("建立文档向量模型");
            optRecord.setOptNum(totalNum);
            optRecord.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            optRecord.setStatus(1);
            queryDBToInsertLog(optRecord);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("文档向量创建处理失败，已完成:" + totalNum);
        }
    }

    public void cluster() {
        //TODO 文本聚类实现

    }

    /**
     * @param pageInfo
     * @return
     * @description 封装查询数据库的时候失败重连的代码
     */
    private List<News> queryDBToGetNews(Map pageInfo) throws Exception {
        //当数据库出现连接异常时尝试重新连接
        boolean tryAgin = true;
        int tryNum = 0;
        List<News> newsList = null;
        while (tryAgin && tryNum < Constant.TRY_MAX) {
            try {
                newsList = newsService.findByPage(pageInfo);
                return newsList;
            } catch (Exception e) {
                e.printStackTrace();
                tryNum++;
                try {
                    Thread.sleep(Constant.WAIT_MILLIS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        LOGGER.error("查询news失败：" + pageInfo);
        throw new Exception();
    }

    private void queryDBToInsertVector(DocumentVector dv) throws Exception {
        //当数据库出现连接异常时尝试重新连接
        boolean tryAgin = true;
        int tryNum = 0;
        while (tryAgin && tryNum < Constant.TRY_MAX) {
            try {
                documentVectorMapper.insert(dv);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                tryNum++;
                try {
                    Thread.sleep(Constant.WAIT_MILLIS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        //插入失败，打印日志
        LOGGER.error("插入文档向量到数据库失败！" + dv);
        throw new Exception();   //抛出异常给调用方法catch
    }

    private void queryDBToInsertLog(OptRecord optRecord) {
        boolean tryAgin = true;
        int tryNum = 0;
        while (tryAgin && tryNum < Constant.TRY_MAX) {
            try {
                optRecordMapper.insert(optRecord);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                tryNum++;
                try {
                    Thread.sleep(Constant.WAIT_MILLIS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        LOGGER.error("插入操作日志到数据库失败！" + optRecord);
    }
}
