package com.xsl.wechat.controller.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin  // 设置跨域请求
@Controller
@RequestMapping("page")
public class ThymeleafController {

    @GetMapping("/index")
    public String index(ModelMap map){
        map.addAttribute("name", "thymeleaf xsl");
        return "thymeleaf/index";
    }







}
