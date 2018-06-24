package com.charles.na.service.impl;

import com.charles.na.service.ISentimentService;
import com.charles.na.utils.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * @author huqj
 */
@Service("sentimentService")
public class SentimentServiceImpl implements ISentimentService {

    Logger LOGGER = Logger.getLogger(SentimentServiceImpl.class);

    /**
     * 分析文本情感倾向的接口地址
     */
    private String url = "http://ictclas.nlpir.org/nlpir/index4/getEmotionResult.do";

    private String PARAM = "content";

    private String KEY = "emotionjson";

    private String defaultJson = "{\"anger\":\"0\",\"evil\":\"0\",\"fear\":\"0\",\"good\":\"1\",\"happy\":\"0\",\"sorrow\":\"0\",\"surprise\":\"0\"}";

    public String parseSentiment(String text) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(PARAM, text);
        String result = HttpUtil.postRequest(url, map);
        if (StringUtils.isBlank(result)) {
            return defaultJson;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> res = mapper.readValue(result, Map.class);
            return res.get(KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defaultJson;
    }

    public int calNegative(Map<String, String> map) {
        int result = 0;
        if (map.get("anger") != null)
            result += Integer.parseInt(map.get("anger"));
        if (map.get("evil") != null)
            result += Integer.parseInt(map.get("evil"));
        if (map.get("fear") != null)
            result += Integer.parseInt(map.get("fear"));
        if (map.get("sorrow") != null)
            result += Integer.parseInt(map.get("sorrow"));
        return result;
    }
}
