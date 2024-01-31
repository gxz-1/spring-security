package com.gxz.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//在Spring Security 5.4.0及更高版本中，WebSecurityConfigurerAdapter 已经被逐渐淘汰
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    //1.通过HttpSecurity定义授权的规则
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security.authorizeHttpRequests(
                (authz) -> authz
                        //首页和登录界面和静态资源所有人都可以访问
                        .requestMatchers("/","/index","/mylogin","/css/**","/pic/**").permitAll()
                        .requestMatchers("/views/level1/**").hasRole("vip1") //level1下有vip1权限才能访问
                        .requestMatchers("/views/level2/**").hasRole("vip2") //level2有vip2权限才能访问
                        .requestMatchers("/views/level3/**").hasRole("vip3") //level3下有vip3权限才能访问
        ).formLogin()//没有权限时跳转到登录页面
                .loginPage("/mylogin")//指定自定义的get登录请求
                .failureUrl("/mylogin?state=failure")//指定登录失败重定向
                .loginProcessingUrl("/loginPost")//指定登录的post请求地址
                .usernameParameter("userName")//指定登录post请求的用户名的key值
                .passwordParameter("passWord")//指定登录post请求的密码的key值
                .and().logout()//开启注销功能,并允许所有用户访问注销URL
                .logoutUrl("/logout")//访问/logout时执行注销,绑定到注销按钮
                .logoutSuccessUrl("/");//注销成功后跳转到/index
//        security.csrf().disable(); // 仅用于调试，不推荐在生产环境禁用CSRF保护
        return security.build();
    }

//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        //对传入的password进行强散列加密
//        return new BCryptPasswordEncoder();
//    }


}