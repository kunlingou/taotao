package utils;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

public class HttpClientUtil {

    public static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(5000)
        .setConnectionRequestTimeout(5000).setSocketTimeout(5000).setRedirectsEnabled(true).build();

    public static void doGet(String url, JSONObject params) {
        HttpGet httpGet = new HttpGet(url + "?" + toJSONString(params));
        httpGet.setConfig(REQUEST_CONFIG);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            CloseableHttpResponse response = httpClient.execute(httpGet);) {
            System.out.println(response.getStatusLine());
            System.out.println(response.getEntity().getContent());
            EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String toJSONString(JSONObject params) {
        if(params == null || params.isEmpty()) return "";
        StringBuffer sb = new StringBuffer();
        for(String key : params.keySet()) {
            Object value;
            sb.append(key + "=" + ((value = params.get(key))==null?"":String.valueOf(value))+"&");
        }
        return String.valueOf(sb.deleteCharAt(sb.lastIndexOf("&")));
    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", null);
        jsonObject.put("age", 23);
        String jsonString = toJSONString(jsonObject);
        System.out.println(jsonString);
        HttpClientUtil.doGet("http://www.baidu.com", jsonObject);
    }
}
