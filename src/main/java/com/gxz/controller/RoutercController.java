package com.gxz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

//在项目中包含了Thymeleaf的依赖，Spring Boot会自动配置Thymeleaf视图解析器
//默认情况下，Thymeleaf模板被放置在templates目录
@Controller
public class RoutercController {

    @RequestMapping({"/","index"})
    public String index(){
        return "index";//走视图解析器访问首页
    }

    @RequestMapping("login")
    public String toLogin(){
        return "views/login";
    }

    @RequestMapping("views/{level}/{num}")
    public String level(@PathVariable String level, @PathVariable String num){
        return "views/"+level+"/"+num;
    }

}
