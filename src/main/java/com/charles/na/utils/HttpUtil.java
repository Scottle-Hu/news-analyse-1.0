package com.charles.na.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装http请求的工具类
 *
 * @author huqj
 */
public class HttpUtil {

    public static Logger LOGGER = Logger.getLogger(HttpUtil.class);

    /**
     * 发出post请求并获取响应的文本形式
     *
     * @param url
     * @param param
     * @return
     */
    public static String postRequest(String url, Map<String, String> param) {
        HttpClient client = new DefaultHttpClient();
        //构造一级请求的url
        HttpPost post = new HttpPost(url);
        //设置请求头信息，防止中文乱码
        post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        post.setHeader("Accept", "text/plain;charset=utf-8");
        //创建参数列表
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> e : param.entrySet()) {
            list.add(new BasicNameValuePair(e.getKey(), e.getValue()));
        }
        //url格式编码
        UrlEncodedFormEntity uefEntity = null;
        try {
            uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post.setEntity(uefEntity);
        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            LOGGER.error("使用httpclient请求接口出现问题");
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        String content = null;
        try {
            content = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭资源
        client.getConnectionManager().shutdown();
        try {  //通过请求头没法解决乱码问
            return new String(content.getBytes("iso-8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getRequest(String url) {
        HttpClient client = new DefaultHttpClient();
        //构造一级请求的url
        HttpGet get = new HttpGet(url);
        //设置请求头信息，防止中文乱码
        get.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        get.setHeader("Accept", "text/plain;charset=utf-8");
        HttpResponse response = null;
        try {
            response = client.execute(get);
        } catch (IOException e) {
            LOGGER.error("使用httpclient请求接口出现问题");
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        String content = null;
        try {
            content = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭资源
        client.getConnectionManager().shutdown();
        try {  //通过请求头没法解决乱码问
            return new String(content.getBytes("iso-8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
