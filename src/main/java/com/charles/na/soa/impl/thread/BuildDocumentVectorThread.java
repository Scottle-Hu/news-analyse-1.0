package com.charles.na.soa.impl.thread;

import com.charles.na.common.Constant;
import com.charles.na.mapper.DocumentVectorMapper;
import com.charles.na.mapper.OptRecordMapper;
import com.charles.na.model.DocumentVector;
import com.charles.na.model.News;
import com.charles.na.model.OptRecord;
import com.charles.na.service.INewsService;
import com.charles.na.soa.impl.NewsSOAServiceImpl;
import com.charles.na.utils.IDUtil;
import com.charles.na.utils.TimeUtil;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * @description 执行分页文本向量建立任务的线程
 */
public class BuildDocumentVectorThread extends Thread {

    private Logger LOGGER = Logger.getLogger(BuildDocumentVectorThread.class);

    /**
     * 分页信息
     */
    private Map<String, Object> pageInfo;

    private INewsService newsService;

    private DocumentVectorMapper documentVectorMapper;

    private OptRecordMapper optRecordMapper;

    private List<Integer> threads;

    @Setter
    private String date;

    public BuildDocumentVectorThread(Map<String, Object> pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Override
    public void run() {
        try {
            if (pageInfo == null || pageInfo.get("pageNo") == null
                    || pageInfo.get("pageSize") == null || pageInfo.get("date") == null) {
                LOGGER.error("分页信息缺失");
                return;
            }
            List<News> newsList = null;
            try {
                newsList = queryDBToGetNews(pageInfo);
                System.out.println(newsList.size());
            } catch (Exception e2) {
                e2.printStackTrace();
                LOGGER.error("查询t_news出现问题");
                return;
            }
            int totalNum = 0;
            for (News news : newsList) {
                try {
                    totalNum++; //记录
                    DocumentVector dv = new DocumentVector();
                    dv.setId(IDUtil.generateLongID());
                    dv.setNewsId(news.getId());
                    Map<String, Integer> map = newsService.splitById(news.getId());
                    String vector = "(";
                    for (Map.Entry<String, Integer> e : map.entrySet()) {
                        vector = vector + e.getKey() + ":" + e.getValue() + " ";
                    }
                    vector = vector.substring(0, vector.length() - (vector.length() > 1 ? 1 : 0)) + ")";
                    dv.setVector(vector);
                    dv.setDate(date);
                    //插入文本向量，同样使用失败重连机制
                    queryDBToInsertVector(dv);
                } catch (Exception e1) {
                    totalNum--;
                    e1.printStackTrace();
                    LOGGER.error("在创建文本" + news.getId() + "时出现问题");
                }
            }

            LOGGER.info("本次文档向量建立过程成功，分页信息:" + pageInfo);
            //将操作成功的记录写入数据库
            OptRecord optRecord = new OptRecord();
            optRecord.setId(IDUtil.generateID());
            optRecord.setOpt("建立文档向量模型成功，分页信息:" + pageInfo);
            optRecord.setOptNum(totalNum);
            optRecord.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            optRecord.setStatus(1);
            queryDBToInsertLog(optRecord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            synchronized (threads) {
                threads.remove(0);
            }
            NewsSOAServiceImpl.waitOrNotify(threads, threads);
        }
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
                newsList = newsService.findByPage(Integer.valueOf(pageInfo.get("pageNo").toString()),
                        Integer.valueOf(pageInfo.get("pageSize").toString()), pageInfo.get("date").toString());
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

    public INewsService getNewsService() {
        return newsService;
    }

    public void setNewsService(INewsService newsService) {
        this.newsService = newsService;
    }

    public DocumentVectorMapper getDocumentVectorMapper() {
        return documentVectorMapper;
    }

    public void setDocumentVectorMapper(DocumentVectorMapper documentVectorMapper) {
        this.documentVectorMapper = documentVectorMapper;
    }

    public OptRecordMapper getOptRecordMapper() {
        return optRecordMapper;
    }

    public void setOptRecordMapper(OptRecordMapper optRecordMapper) {
        this.optRecordMapper = optRecordMapper;
    }

    public List<Integer> getThreads() {
        return threads;
    }

    public void setThreads(List<Integer> threads) {
        this.threads = threads;
    }
}
