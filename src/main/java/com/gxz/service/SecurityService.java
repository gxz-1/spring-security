package com.gxz.service;

import com.gxz.mapper.UserMapper;
import com.gxz.pojo.MyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SecurityService {
    /**
     * spring security会自动处理登录以及注销的请求，完成数据库的增删改查
     * 0. JdbcUserDetailsManager实现了数据库操作，需要定义数据库表（或者继承UserDetailsService自己写mapper）
     * 0. 在config中SecurityFilterChain定义了授权配置，根据配置写contorller以及登录页面
     * 1. 访问未授权地址时get自动跳转到登录页面
     * 2. 输入账号密码后点击登录post请求，由spring security收集请求参数
     * 3. spring security调用UserDetailsService.loadUserByusername查找数据库中的用户名
     * 4. 调用PasswordEncoder.matches比较密码，进行登录
     */

    @Autowired
    JdbcUserDetailsManager detailsManager;
    @Autowired
    PasswordEncoder encoder;

    public String Register(String userName,String passWord,String groupName){
        if(detailsManager.userExists(userName)){
            return "0";
        }
        //查询组对应的权限
        List<GrantedAuthority> authorities = detailsManager.findGroupAuthorities(groupName);
        //根据用户名、密码、组权限创建用户
        UserDetails user=User.withUsername(userName).password(encoder.encode(passWord)).authorities(authorities).build();
        detailsManager.createUser(user);
        //将用户添加到组中
        detailsManager.addUserToGroup(userName,groupName);
        return "1";
    }
//    @Autowired
//    private PasswordEncoder encoder;

    //对密码进行加密
//    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    String password = encoder.encode("password");
//    //创建用户
//    UserDetails user1 = User.withUsername("user1").password(password).roles("vip1").build();
//    UserDetails user2 = User.withUsername("user2").password(password).roles("vip2").build();
//    UserDetails user3 = User.withUsername("user3").password(password).roles("vip3").build();
//    UserDetails admin = User.withUsername("admin").password(password).roles("vip1","vip2","vip3").build();
//    //添加用户并返回
//    if(!userDetailsManager.userExists("user1")) userDetailsManager.createUser(user1);
//    if(!userDetailsManager.userExists("user2")) userDetailsManager.createUser(user2);
//    if(!userDetailsManager.userExists("user3")) userDetailsManager.createUser(user3);
//    if(!userDetailsManager.userExists("admin")) userDetailsManager.createUser(admin);

}
