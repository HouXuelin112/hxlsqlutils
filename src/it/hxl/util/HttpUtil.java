package it.hxl.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

public class HttpUtil {
    /**
     * 模拟get请求
     * @param url
     * @return
     */
    public static String sendGet(String url) {
        String response = null;
        CloseableHttpClient aDefault = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse execute = aDefault.execute(httpGet);
            response = EntityUtils.toString(execute.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (aDefault != null) {
                try {
                    aDefault.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return response;
    }

    /**
     *
     * @param url "http://127.0.0.1:8083/shortageInfo/getSingle"
     * @param data {'shortageInfo': {'id':'105'}}
     * @param mimeType mimeType
     * @param charset charset
     * @return response
     */
    public static String sendPost(String url, String data, String mimeType, String charset) {
        String response = null;
        try {
            CloseableHttpClient httpclient = null;
            CloseableHttpResponse httpResponse = null;
            try {
                httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost(url);
                HttpEntity stringentity = new StringEntity(data, ContentType.create(mimeType, charset));
                httppost.setEntity(stringentity);
                httpResponse = httpclient.execute(httppost);
                response = EntityUtils
                        .toString(httpResponse.getEntity());
                System.out.println(response);

            } finally {
                if (httpclient != null) {
                    httpclient.close();
                }
                if (httpResponse != null) {
                    httpResponse.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 模拟post请求
     * @param url url
     * @param data data
     * @return 返回对象字符串
     */
    public static String sendPost(String url, String data) {
        return sendPost(url, data, "application/json", "UTF-8");
    }
}
