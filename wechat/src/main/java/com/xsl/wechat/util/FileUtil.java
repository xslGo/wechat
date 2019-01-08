package com.xsl.wechat.util;

import java.io.*;

public class FileUtil {

    /**
     * 把accessToken写入accessToken.txt文件中
     * @param accessToken 微信API调用需要用到的accessToken
     */
    public static void writeAccessTokenFile(String accessToken) {
        try{
            // 相对路径,如果没有则创建一个新的accessToken文件
            File file = new File("accessToken.txt");
            // 创建新文件,有同名的直接覆盖
            file.createNewFile();
            try(BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
                out.write(accessToken);

            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     *  读取accessToken.txt
     * @return  accessToken
     */
    public static String readAccessTokenFile() {
        String accessToken = null;
        String pathName = "accessToken.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(pathName))) {
            accessToken = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

}
