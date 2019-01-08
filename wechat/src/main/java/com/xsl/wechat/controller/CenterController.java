package com.xsl.wechat.controller;

import com.xsl.wechat.controller.wxcontroller.TokenCheckController;
import com.xsl.wechat.util.SpecificMessageHandlerUtil;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("centerController")
public class CenterController {

    @Resource
    private TokenCheckController tokenCheckController;

    @RequestMapping("wxCenter")
    @ResponseBody
    public void centerWx(HttpServletRequest request, HttpServletResponse response) throws IOException, DocumentException {

        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        // 响应信息
        String responseMessage;
        if("GET".equals(request.getMethod())) {
            // 验证是否来自微信服务器
            responseMessage = tokenCheckController.tokenCheck(request);
            response.getWriter().write(responseMessage);

        }else if("POST".equals(request.getMethod())) {
            Map map = SpecificMessageHandlerUtil.parseXml(request);
            responseMessage = SpecificMessageHandlerUtil.buildResponseMessage(map);
            response.getWriter().write(responseMessage);
        }
    }
}
