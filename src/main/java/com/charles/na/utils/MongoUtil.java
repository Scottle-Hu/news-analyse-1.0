package com.charles.na.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.Map;

/**
 * @author huqj
 */
public class MongoUtil {

    /**
     * 数据库连接对象，可以复用
     */
    private MongoDatabase mongoDatabase;

    private MongoClient mongoClient;

    /**
     * 连接数据库的相关参数
     */
    private String host;  //mongo地址
    private Integer port; //端口号
    private String username; //用户名
    private String password; //密码
    private String databaseName; //数据库名称

    /**
     * @param params 获取数据库连接的参数键值对集合
     */
    public MongoUtil(Map<String, Object> params) {
        host = (String) params.get("host");
        port = (Integer) params.get("port");
        username = (String) params.get("username");
        password = (String) params.get("password");
        databaseName = (String) params.get("databaseName");
    }

    /**
     * @return 数据库连接对象
     */
    public MongoDatabase getMongoDatabase() {
        try {
            if (mongoDatabase != null) {
                return mongoDatabase;
            } else {
                if (host == null || port == null || databaseName == null) {
                    return null;
                } else {
                    if (username == null || password == null) { //不使用验证的连接
                        mongoClient = new MongoClient(host, port);
                        mongoDatabase = mongoClient.getDatabase(databaseName);
                        return mongoDatabase;
                    } else { //使用验证
                        //TODO
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

}
