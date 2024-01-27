package com.gxz.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//在Spring Security 5.4.0及更高版本中，WebSecurityConfigurerAdapter 已经被逐渐淘汰
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    //1.通过HttpSecurity定义授权的规则
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                (authz) -> authz
                        .requestMatchers("/","/css/**","/pic/**").permitAll()  //首页和静态资源所有人都可以访问
                        .requestMatchers("/views/level1/**").hasRole("vip1") //level1下有vip1权限才能访问
                        .requestMatchers("/views/level2/**").hasRole("vip2") //level2有vip2权限才能访问
                        .requestMatchers("/views/level3/**").hasRole("vip3") //level3下有vip3权限才能访问
        ).formLogin();//没有权限时跳转到登录页面
        return http.build();
    }

    @Bean
    //2.通过UserDetailsService添加用户认证的规则
    public UserDetailsService userDetailsService() {
        //对密码进行加密
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String password = encoder.encode("password");
        //创建用户
        UserDetails user1 = User.withUsername("user1").password(password).roles("vip1").build();
        UserDetails user2 = User.withUsername("user2").password(password).roles("vip2").build();
        UserDetails user3 = User.withUsername("user3").password(password).roles("vip3").build();
        UserDetails admin = User.withUsername("admin").password(password).roles("vip1","vip2","vip3").build();
        return new InMemoryUserDetailsManager(user1,user2,user3,admin);
    }



}