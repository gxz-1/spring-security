package com.gxz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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
        //查询组对应的权限（源码中表名groups是sql的关键字，需要重写sql，给groups添加反引号）
        detailsManager.setGroupAuthoritiesSql("SELECT g.id, g.group_name, ga.authority " +
                "FROM `groups` g, group_authorities ga WHERE g.group_name = ? AND g.id = ga.group_id");
        List<GrantedAuthority> authorities = detailsManager.findGroupAuthorities(groupName);
        //根据用户名、密码、组权限创建用户
        UserDetails user=User.withUsername(userName).password(encoder.encode(passWord)).authorities(authorities).build();
        detailsManager.createUser(user);
        //将用户添加到组中(同理，根据源码重写sql)
        detailsManager.setFindGroupIdSql("select id from `groups` where group_name = ?");
        detailsManager.addUserToGroup(userName,groupName);
        return "1";
    }

}
