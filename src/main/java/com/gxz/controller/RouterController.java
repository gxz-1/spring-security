package com.gxz.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//在项目中包含了Thymeleaf的依赖，Spring Boot会自动配置Thymeleaf视图解析器
//默认情况下，Thymeleaf模板被放置在templates目录
@Controller
public class RouterController {

    @RequestMapping({"/","index"})
    public String index(Model model){
        //通过SecurityContextHolder的静态方法获取已登录的授权信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName().equals("anonymousUser")? "请先登录" : "你好！"+auth.getName();
        //thymeleaf通过Model形参向前端传递字符串参数
        model.addAttribute("userName",name);
        return "index";//走视图解析器访问首页
    }

    @RequestMapping(value = "mylogin",method = RequestMethod.GET)
    public String toLogin(@RequestParam(required = false) String state){
        if(state!=null){
            System.out.println("登录失败！");
        }
        return "views/login";
    }

    @RequestMapping("views/{level}/{num}")
    public String level(@PathVariable String level, @PathVariable String num){
        return "views/"+level+"/"+num;
    }

    @RequestMapping("AccessDenied")
    public String denied(){
        return "views/unauthorized";
    }

}
