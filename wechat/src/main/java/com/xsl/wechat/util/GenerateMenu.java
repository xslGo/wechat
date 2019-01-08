package com.xsl.wechat.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

public class GenerateMenu {

    /**
     * 创建菜单
     * @param file 菜单的json格式
     * @return
     * @throws IOException
     */
    public static String sendPostRequest(File file) throws IOException {
        String content = null;
        // 请求URL
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
        String requestUrl = url.replace("ACCESS_TOKEN", FileUtil.readAccessTokenFile());

        // 创建http post 请求
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(requestUrl);
        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000)  // 设置从connect Manager(连接池)获取Connection 超时时间，单位毫秒
                .setConnectTimeout(5000)    // 设置连接超时时间，单位毫秒
                .setSocketTimeout(5000)     // 请求获取数据的超时时间(即响应时间),单位毫秒.如果访问一个接口.多少时间内无法返回数据,就直接放弃此次调用
                .build();
        httpPost.setConfig(requestConfig);
        // 构建消息实体
        FileEntity fileEntity = new FileEntity(file, ContentType.create("text/plain", "UTF-8"));
        // 设置请求数据
        httpPost.setEntity(fileEntity);
        CloseableHttpResponse response =  httpClient.execute(httpPost);
        // 获取响应内容
        HttpEntity entity = response.getEntity();
        if(entity != null){
            BufferedHttpEntity bufferEntity = new BufferedHttpEntity(entity);
            InputStream inputStream = bufferEntity.getContent();
            content = inputStreamToString(inputStream);
        }
        // 关闭响应
        response.close();
        // 关闭客户端
        httpClient.close();
        return content;
    }

    /**
     * 输入流转为字符串
     * @param inputStream 输入流
     * @return 转换成的字符
     * @throws IOException
     */
    private static String inputStreamToString(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();
        String str;
        while ((str = bufferedReader.readLine()) != null){
            builder.append(str);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        return builder.toString();
    }


    public static void main(String[] args) throws IOException {
        File file = new File("wxMenu.txt");
        String content = sendPostRequest(file);
        if(content != null){
            JSONObject jsonData = JSONObject.parseObject(content);
            /**
             *  成功后返回的json数据 => {"errcode":0,"errmsg":"ok"}
             */
            System.out.println("jsonData =>" + jsonData.toString());
            String result = (String) jsonData.get("errmsg");
            if("ok".equals(result)){
                System.out.println("成功创建菜单！！！");
            }
        }
    }
}
