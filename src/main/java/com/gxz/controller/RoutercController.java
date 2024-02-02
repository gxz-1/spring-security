package com.gxz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//在项目中包含了Thymeleaf的依赖，Spring Boot会自动配置Thymeleaf视图解析器
//默认情况下，Thymeleaf模板被放置在templates目录
@Controller
public class RoutercController {

    @RequestMapping({"/","index"})
    public String index(){
        System.out.println("index");
        return "index";//走视图解析器访问首页
    }

    @RequestMapping(value = "mylogin",method = RequestMethod.GET)
    public String toLogin(@RequestParam(required = false) String state){
        System.out.println("mylogin");
        if(state!=null){
            System.out.println("登录失败！");
        }
        return "views/login";
    }

    @RequestMapping("views/{level}/{num}")
    public String level(@PathVariable String level, @PathVariable String num){
        System.out.println("views");
        return "views/"+level+"/"+num;
    }

    @RequestMapping("AccessDenied")
    public String denied(){
        return "views/unauthorized";
    }

}
