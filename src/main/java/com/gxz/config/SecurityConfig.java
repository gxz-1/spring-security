package com.gxz.config;


import com.gxz.handler.LoginFailureHandler;
import com.gxz.handler.LoginSuccessHandler;
import com.gxz.handler.LoginoutHandler;
import com.gxz.handler.MyAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

//在Spring Security 5.4.0及更高版本中，WebSecurityConfigurerAdapter 已经被逐渐淘汰
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private LoginFailureHandler loginFailureHandler;
    @Autowired
    private LoginoutHandler loginoutHandler;


    @Bean
    //1.通过HttpSecurity定义授权的规则
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        //1.登录配置
        security.formLogin()//没有权限且未登录时跳转到登录页面
                .loginPage("/mylogin")//指定自定义的get登录请求
                .loginProcessingUrl("/loginPost")//指定登录的post请求地址
                .usernameParameter("userName")//指定登录post请求的用户名的key值
                .passwordParameter("passWord")//指定登录post请求的密码的key值
//                .defaultSuccessUrl("/")//直接访问登录页面时，成功后重定向地址
//                .successForwardUrl("/")//所有情况下，认证成功后的请求转发地址
                .successHandler(loginSuccessHandler)//设置登录成功后的处理代码逻辑
//                .failureUrl("/mylogin?state=failure")//指定登录失败重定向
                .failureHandler(loginFailureHandler);//设置登录失败后的处理代码逻辑

        //2.退出配置
        security.logout()//开启注销功能,并允许所有用户访问注销URL
                .logoutUrl("/logout")//访问/logout时执行注销,绑定到注销按钮
//                .logoutSuccessUrl("/index")//退出成功后跳转到/index
                .logoutSuccessHandler(loginoutHandler);//设置退出登录的处理代码逻辑

        //3.配置rememberme信息
        security.rememberMe((config)->config
                .tokenRepository(persistentTokenRepository())//调用方法，获取对象
                .rememberMeParameter("remember-me")//设置Post请求参数key
                .rememberMeCookieName("BJSXT_REM")//客户端保存cookie
                .rememberMeCookieDomain("202.115.17.90")//cookie的domain，默认自动解析
                .tokenValiditySeconds(18000)//cookie有效时间，单位s秒
//                .userDetailsService();//如果自定义了UserDetailsService接口实现对象，需要传入
        );

        //4.授权配置(基于角色ROLE的授权管理)
        security.authorizeHttpRequests(
                (authz) -> authz
                        .requestMatchers("/","/index","/mylogin","/AccessDenied").permitAll()
                        .requestMatchers(HttpMethod.GET,"/css/**","/pic/**").permitAll()//限定仅GET能访问
                        .requestMatchers("/views/level1/**").hasRole("vip1") //level1下有vip1权限才能访问
                        .requestMatchers("/views/level2/**").hasRole("vip2") //level2有vip2权限才能访问
                        .requestMatchers("/views/level3/**").hasRole("vip3") //level3下有vip3权限才能访问
//                        .requestMatchers("").hasAnyRole("vip1","vip2")//vip1,vip2只要有任一权限都可以访问
                        .anyRequest().authenticated()//其他地址需要授权，要放在最后一行
                        //数据库中存储"ROLE_vip1",hasRole自动添加"ROLE_"前缀
        );

        //5.自定义无权限界面
        security.exceptionHandling(
                //设置自定义无权访问的处理代码逻辑
                (config)->config.accessDeniedHandler(new MyAccessDeniedHandler())
        );


        security.csrf().disable(); // 仅用于调试，不推荐在生产环境禁用CSRF保护

        return security.build();
    }

    @Bean
    //配置UserDetailsService的数据库连接
    public UserDetailsService userDetailsService() {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    //基于datasource把需要rememberme的数据保存到数据库
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl repository=new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
//        repository.setCreateTableOnStartup(true);//初始化创建表格，仅使用一次
        return repository;
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        //对传入的password进行强散列加密
        return new BCryptPasswordEncoder();
    }

}