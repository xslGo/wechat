package com.xsl.wechat.common;

import com.alibaba.fastjson.JSONObject;
import com.xsl.wechat.enumeration.WeChatEnum;
import com.xsl.wechat.util.FileUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 每过1小时55分钟发送一次https请求获取AccessToken
 */

public class AccessToken {
    public static String setAccessTokenToFile() throws Exception{
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String weiXinUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
                String requestWeiXinUrl = weiXinUrl.replace("APPID", WeChatEnum.APPID.getValue())
                        .replace("APPSECRET", WeChatEnum.APPSECRET.getValue());

                JSONObject jsonObject = null;
                try {
                    jsonObject = AccessToken.sendHttpRequest(requestWeiXinUrl, "GET");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(jsonObject != null) {
                    FileUtil.writeAccessTokenFile(jsonObject.getString("access_token"));
                    System.out.println("access_token保存到文件中 =>" + jsonObject.getString("access_token"));
                }
            }
        },0,7200 - (5*60), TimeUnit.SECONDS);
        return "OK";
    }


    /**
     * @param url       请求的url服务器地址
     * @param method    请求的方法支持GET,POST方法
     * @return          json对象
     */
    private static JSONObject sendHttpRequest(String url, String method) throws Exception {
        JSONObject jsonObject = null;
        URL connUrl = new URL(url);
        HttpURLConnection connection =  (HttpsURLConnection) connUrl.openConnection();
        connection.setRequestMethod(method);
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(5000);
        connection.connect();

        // 正常返回代码：200
        if(connection.getResponseCode() == 200){
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                stringBuffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            connection.disconnect();
            jsonObject = JSONObject.parseObject(stringBuffer.toString());
        }
        return jsonObject;
    }






}
