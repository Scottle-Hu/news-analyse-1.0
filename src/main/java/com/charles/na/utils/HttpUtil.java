package com.charles.na.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
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
    public static String postRequest(String url, Map<String, String> param, String encoding) {
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
            if (encoding != null)
                return new String(content.getBytes(encoding), "utf-8");
            return content;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getRequest(String url, String encoding) {
        HttpClient client = new DefaultHttpClient();
        try {
            //构造一级请求的url
            HttpGet get = new HttpGet(url);
            //设置请求头信息，防止中文乱码
            get.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            get.setHeader("Accept", "text/plain;charset=utf-8");

            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            if (encoding != null)
                return new String(content.getBytes(encoding), "utf-8");
            return content;
        } catch (Exception e) {
            LOGGER.error("error when send GET request to get response. url=" + url, e);
        } finally {
            //关闭资源
            client.getConnectionManager().shutdown();
        }
        return null;
    }

    /**
     * 网上copy，据说可以解决https证书验证的问题
     *
     * @return
     */
    private static DefaultHttpClient getHttpClient() {
        try {
            // 禁止https证书验证
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);//运行所有的hostname验证

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            // 禁用Cookie2请求头
            HttpClientParams.setCookiePolicy(params, CookiePolicy.RFC_2109);
            HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
            HttpClientParams.setCookiePolicy(params, CookiePolicy.NETSCAPE);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);
            return new DefaultHttpClient(httpParams);
        }
    }

}
