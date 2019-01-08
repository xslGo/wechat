package com.xsl.wechat.util;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class GenerateQRCodeUtil {


    /**
     * 请求url
     * @return url
     */
    public static String getRequestUrl(){
        // 微信获取ticket接口
        String url  = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN";
        return url.replace("TOKEN", FileUtil.readAccessTokenFile());
    }

    /**
     * @param time  过期时间，为空时，获取永久二维码
     * @param sceneId 场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
     * @return 请求体
     */
    public static String getRequestParam(Integer time, String sceneId) {
        // 过期时间为空时时，此json数据为获取永久二维码的数据
        String requestData;
        if(time == null || "".equals(time)){
            System.out.println("获取永久二维码");
            requestData = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": \""+ sceneId +"\"}}}";
        }else {
            System.out.println("获取临时二维码");
            requestData = "{\"expire_seconds\": \""+ time +"\", \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": \""+ sceneId +"\"}}}";
        }
        return requestData;
    }

    /**
     * 以post方式发送https请求
     * @param url  请求url
     * @param param 请求体
     * @return 微信服务器响应数据
     */
    public static String sendPostRequest(String url, String param){
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder responseResult = new StringBuilder();
        try {
            URL requestUrl = new URL(url);
            URLConnection connection = requestUrl.openConnection();
            // 设置通用属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送post请求需设置下面两个为true
            connection.setDoInput(true);
            connection.setDoOutput(true);
            out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            // 带参数发送请求
            out.print(param);
            out.flush();
            // 读取URL响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = in.readLine()) != null){
                responseResult.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                out.close();
            }
        }
        return responseResult.toString();
    }


    /**
     * 从网络中URL下载文件
     * @param ticket   换取二维码票据
     * @param fileName 文件名
     * @param savePath 保存路径
     * @throws IOException 抛出异常
     */
    public static void downloadQRCode(String ticket, String fileName, String savePath) throws IOException {
        String urlStr = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";
        String requestUrl = urlStr.replace("TICKET", ticket);
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 设置超时时间为5秒
        connection.setConnectTimeout(5000);
        //防止屏蔽程序抓取而返回403错误
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        // 得到输入流
        InputStream inputStream = connection.getInputStream();
        // 获取自己的数组
        byte[] getData = readInputStream(inputStream);
        // 文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()) {
            saveDir.mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos != null){
            fos.close();
        }
        if(inputStream != null){
            inputStream.close();
        }
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new  byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1){
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }



    public static void main(String[] args) {

        String url = getRequestUrl();
        // 临时二维码  第一个参数为有效时间，第二个为场景值id,为32位非零整形
        String temporaryParam = getRequestParam(300, "12345678901234567890123456789012");
        // 发送请求
        String responseResult1 = sendPostRequest(url, temporaryParam);


        // 永久二维码，第一个参数为空，第二个参数最大值为100000（目前参数只支持1--100000）
//        String foreverParam = getRequestParam(null, "100");
        // 发送请求
//        String responseResult2 = sendPostRequest(url, foreverParam);

        // 转换为JSON
        JSONObject resultJSON = JSONObject.parseObject(responseResult1);
        //  打印JSON
        /** 数据格式：
         *  {
         *      "ticket":"gQEm7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyR1lNeHNURmtjdWsxYUVrbnhzMWgAAgR8kxdcAwQsAQAA",
         *      "expire_seconds":300,
         *      "url":"http://weixin.qq.com/q/02GYMxsTFkcuk1aEknxs1h"
        *    }
         */
        System.out.println("resultJSON => " + resultJSON);

        String ticket = (String) resultJSON.get("ticket");
        try {
            downloadQRCode(ticket,"code.jpg", "E:\\QRCode\\code");
            System.out.println("下载成功");
        } catch (IOException e) {
            System.out.println("下载失败");
            e.printStackTrace();
        }


    }










}
