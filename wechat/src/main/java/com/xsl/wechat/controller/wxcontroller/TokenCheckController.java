package com.xsl.wechat.controller.wxcontroller;

import com.xsl.wechat.enumeration.WeChatEnum;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class TokenCheckController {

    public String tokenCheck(HttpServletRequest request) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        System.out.println("返回echostr到微信服务器");
        return TokenCheckController.tokenCheck(signature, WeChatEnum.TOKEN.getValue(), timestamp, nonce, echostr);
    }




    /**
     * 验证消息的确来自微信服务器:
     *   1.将token、timestamp、nonce三个参数进行字典序排序(token为开发者填写的配置服务器token)
     *   2.将三个参数字符串拼接成一个字符串进行sha1加密
     *   3.获得加密后的字符串可与signature对比,标识该请求来源于微信
     * @param signature  微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     * @param token      令牌
     * @param timestamp  时间戳
     * @param nonce      随机数
     * @param echostr    随机字符串
     * @return  如果是来自微信消息返回echostr，否则返回null
     */
    private static String tokenCheck(String signature, String token, String timestamp, String nonce, String echostr) {

        List<String> list = new ArrayList<>();
        list.add(token);
        list.add(timestamp);
        list.add(nonce);
        // 字典排序
        Collections.sort(list);
        // sha1加密
        String she1Str = DigestUtils.sha1Hex(list.get(0) + list.get(1) + list.get(2));

        // 判断是否是微信信息
        if(she1Str.equals(signature)) {
            return echostr;
        }
        return null;
    }



}
