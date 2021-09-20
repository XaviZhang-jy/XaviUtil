package com.example.Util;


import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 支持form-data,json
 * application/x-www-form-urlencoded: 表单数据，数据放到了url上，用 & 分隔
 * multipart/form-data: 数据以块的形式发送，通常人们使用它来上传文件（图像/音频等）
 * application/json: 数据在JSON format.Ref 1.2
 */
public class HttpData {

    /**
     * 适用x-www-form-urlencoded方式：url携带参数
     */
    public static URI getUrl(String url, Map<String, String> param) {
        URIBuilder builder = null;
        URI uri = null;
        try {
            builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            uri = builder.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

    public static URI getUrl(String url, Object data, Class<?> clazz) {
        URIBuilder builder = null;
        URI uri = null;
        try {
            builder = new URIBuilder(url);
            if (data != null) {
                Field[] allFields = FieldUtils.getAllFields(clazz);
                for (Field field : allFields) {
                    String name = field.getName();
                    if ("serialVersionUID".equals(name)) {
                        continue;
                    }
                    field.setAccessible(true);
                    if(field.get(data) != null){
                        builder.addParameter(field.getName(), field.get(data).toString());
                    }
                }
            }
            uri = builder.build();
        } catch (URISyntaxException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return uri;
    }

    /**
     * 适用于请求体携带：form-data类型
     * 注意：也适用于x-www-form-urlencoded(springboot web)，但不建议用，会有误区
     */
    public static HttpEntity getEntityFormData(Object obj, Class clazz) {
        List<NameValuePair> pairList = new ArrayList<>();
        if (obj == null) {
            return null;
        }
        try {
            Field[] allFields = FieldUtils.getAllFields(clazz);
            for (Field field : allFields) {
                String name = field.getName();
                if ("serialVersionUID".equals(name)) {
                    continue;
                }
                field.setAccessible(true);
                String value = field.get(obj) == null ? null : field.get(obj).toString();
                NameValuePair pair = new BasicNameValuePair(name, value);
                pairList.add(pair);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UrlEncodedFormEntity(pairList, StandardCharsets.UTF_8);
    }

    /**
     * 适用于请求体携带：JSON类型
     */
    public static HttpEntity getEntityJson(String str) {
        return new StringEntity(str, ContentType.APPLICATION_JSON);
    }

    public static Header[] getDefaultHeader(String token) {
        return new Header[]{new BasicHeader("Authorization", token)};
    }

    public static String dealResponse(CloseableHttpResponse response) throws Exception {
        String resultString = null;
        String successStatus = "20";
        try {
            if (response != null && String.valueOf(response.getStatusLine().getStatusCode()).contains(successStatus)) {
                resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultString;
    }

}
