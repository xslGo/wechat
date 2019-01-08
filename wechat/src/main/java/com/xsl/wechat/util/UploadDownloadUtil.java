package com.xsl.wechat.util;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 临时素材上传下载，3天有效期
 * 视频下载用http请求，未实现
 */
public class UploadDownloadUtil {

    /**
     * http请求方式：POST/FORM,使用https
     * https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE
     */
    private static final String UPLOAD_MEDIA_FILE = "https://api.weixin.qq.com/cgi-bin/media/upload";

    /**
     * http请求方式: GET,https调用
     * 注：视频文件不支持https下载，用http协议下载
     */
    private static final String DOWNLOAD_MEDIA_FILE = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";


    /**
     * 微信服务器素材上传
     * @param file        form-data中媒体文件标识，有filename、filelength、content-type等信息
     * @param accessToken 调用微信API的accessToken
     * @param type        支持四种类型素材(video/image/voice/thumb)
     * @return 微信服务器返回的json数据
     */
    private static JSONObject uploadMediaFile(File file, String accessToken, String type) {
        if (file == null || accessToken == null || type == null) {
            return null;
        }
        if (!file.exists()) {
            System.out.println("文件不存在！");
        }
        JSONObject jsonObject = null;
        PostMethod postMethod = new PostMethod(UPLOAD_MEDIA_FILE);
        postMethod.setRequestHeader("Connection", "Keep-Alive");
        postMethod.setRequestHeader("Cache-Control", "no-cache");
        HttpClient httpClient = new HttpClient();
        // 信任任何类型的证书
        Protocol myHttps = new Protocol("https", new SSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myHttps);

        FilePart media;
        try {
            media = new FilePart("media", file);
            Part[] parts = new Part[]{new StringPart("access_token", accessToken), new StringPart("type", type), media};
            MultipartRequestEntity entity = new MultipartRequestEntity(parts, postMethod.getParams());
            postMethod.setRequestEntity(entity);
            // 执行post方法
            int status = httpClient.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                // 获取微信服务器返回的信息
                String responseMessage = postMethod.getResponseBodyAsString();
                jsonObject = JSONObject.parseObject(responseMessage);
            } else {
                System.out.println("上传媒体文件失败，响应转态码:" + status);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 多媒体上传接口
     * @param filePath 媒体文件的绝对路径
     * @param type     媒体文件类型,分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
     * @return json  如：{"media_id":"dO2vohwnd2GMuXbPeO1QDQgtpngRWuJJ__83vZjXID-n65H3wNyvqCx5lE_Kn3q2","created_at":1544871704,"type":"image"}
     */
    public static JSONObject uploadMediaFile(String filePath, String type) {
        // 获取本地文件
        File file = new File(filePath);
        String accessToken = FileUtil.readAccessTokenFile();
        return uploadMediaFile(file, accessToken, type);
    }

    // ---------------------------------- 分割 ---------------------------------


    /**
     * 以http方式发送请求,并将请求响应内容输出到文件
     * @param fileName 存储的文件名
     * @param path   请求路径
     * @param method 请求方法
     * @param body   请求数据
     * @return 返回响应的存储到文件
     */
    public static File httpRequestToFile(String fileName, String path, String method, String body) {
        if (fileName == null || path == null || method == null) {
            return null;
        }

        File file = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        FileOutputStream fileOut = null;
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(method);
            if (null != body) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(body.getBytes("UTF-8"));
                outputStream.close();
            }

            inputStream = conn.getInputStream();
            if (inputStream != null) {
                file = new File(fileName);
            } else {
                return file;
            }

            //写入到文件
            fileOut = new FileOutputStream(file);
            if (fileOut != null) {
                int c = inputStream.read();
                while (c != -1) {
                    fileOut.write(c);
                    c = inputStream.read();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            /*
             * 必须关闭文件流
             * 否则JDK运行时，文件被占用其他进程无法访问
             */
            try {
                inputStream.close();
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 多媒体文件的下载
     * @param accessToken 认证token
     * @param mediaId     素材ID（对应上传后获取到的ID）
     * @param filePath    素材文件存储路径
     * @return 素材文件
     * @comment 不支持视频文件的下载
     */
    private static File downloadMediaFile(String accessToken, String mediaId, String filePath) {
        String url = String.format(DOWNLOAD_MEDIA_FILE, accessToken, mediaId);
        return httpRequestToFile(filePath, url, "GET", null);
    }

    /**
     * 多媒体下载接口
     * @param filePath 素材文件存储路径
     * @param mediaId  素材Id（对应文件上传后获取的Id）
     * @return 文件
     * @comment 不支持视频下载
     */
    public static File downloadMediaFile(String filePath, String mediaId) {
        String accessToken = FileUtil.readAccessTokenFile();
        return downloadMediaFile(accessToken, mediaId, filePath);
    }


    public static void main(String[] args) {
        // 媒体文件路径，绝对路径
        String filePath = "E:/Idea_project/wechat/wechat/src/main/resources/media/image/路飞.jpg";
        // 媒体文件类型
        String type = "static/media/image";

        // 媒体文件路径，绝对路径
//        String filePath = "E:/Idea_project/wechat/wechat/src/main/resources/media/voice/voice.mp3";
        // 媒体文件类型
//        String type = "voice";

        // 媒体文件路径，绝对路径
//        String filePath = "E:/Idea_project/wechat/wechat/src/main/resources/media/video/小苹果.mp4";
        // 媒体文件类型
//        String type = "video";

        // 媒体文件路径，绝对路径
//        String filePath = "";
        // 媒体文件类型
//        String type = "thumb";


        JSONObject uploadResult = uploadMediaFile(filePath, type);
        System.out.println("上传媒体文件返回的信息 =>" + uploadResult.toString());

//         下载刚刚上传的图片
        String mediaId = uploadResult.getString("media_id");
        File file = downloadMediaFile("E:/路飞.png", mediaId);
    }
}
