package com.gxz.config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

//在Spring Security 5.4.0及更高版本中，WebSecurityConfigurerAdapter 已经被逐渐淘汰
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    //1.通过HttpSecurity定义授权的规则
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                (authz) -> authz
                        .requestMatchers("/","/index","/css/**","/pic/**").permitAll()  //首页和静态资源所有人都可以访问
                        .requestMatchers("/views/level1/**").hasRole("vip1") //level1下有vip1权限才能访问
                        .requestMatchers("/views/level2/**").hasRole("vip2") //level2有vip2权限才能访问
                        .requestMatchers("/views/level3/**").hasRole("vip3") //level3下有vip3权限才能访问
        ).formLogin()//没有权限时跳转到登录页面
                .and().logout()//开启注销功能
                .logoutUrl("/logout")//访问/logout时执行注销,绑定到注销按钮
                .logoutSuccessUrl("/");//注销成功后跳转到/index

        http.logout();
        return http.build();
    }

    @Autowired
    private DataSource dataSource;

    @Bean
    //2.通过UserDetailsService添加用户认证的规则
    public UserDetailsService userDetailsService() {
        //连接数据库
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        //对密码进行加密
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String password = encoder.encode("password");
        //创建用户
        UserDetails user1 = User.withUsername("user1").password(password).roles("vip1").build();
        UserDetails user2 = User.withUsername("user2").password(password).roles("vip2").build();
        UserDetails user3 = User.withUsername("user3").password(password).roles("vip3").build();
        UserDetails admin = User.withUsername("admin").password(password).roles("vip1","vip2","vip3").build();
        //添加用户并返回
        if(!userDetailsManager.userExists("user1")) userDetailsManager.createUser(user1);
        if(!userDetailsManager.userExists("user2")) userDetailsManager.createUser(user2);
        if(!userDetailsManager.userExists("user3")) userDetailsManager.createUser(user3);
        if(!userDetailsManager.userExists("admin")) userDetailsManager.createUser(admin);
        return  userDetailsManager;

    }



}