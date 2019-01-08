package com.xsl.wechat.init;

import com.xsl.wechat.common.AccessToken;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 初始化微信服务，*发送请求获取AccessToken<br/>
 * 调用微信API时，通过 FileUtil.readAccessTokenFile()  获取accessToken
 */
@Component
@Order(value = 1)
public class InitWx implements CommandLineRunner {

    /*
     *  @Order注解规定了CommandLineRunner实例的运行顺序。@order(value=2) value 的值从小到大依次执行。
     */
    @Override
    public void run(String... args) throws Exception {
        AccessToken.setAccessTokenToFile();
    }
}
