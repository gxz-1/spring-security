package com.gxz.service;

import com.gxz.mapper.UserMapper;
import com.gxz.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    private UserMapper userMapper;

    //在一定时间内登录失败超过一定次数，锁定用户
    public String LoginFailure(String userName, Integer lockCounts, Long lockDuration){
        User user = userMapper.selectLoginStatus(userName);
        Date currentTime = new Date();
        if(user.getCounts()>=lockCounts){
            if( currentTime.getTime() - user.getLasttime().getTime()>=lockDuration){
                user.setCounts(1);//超过时间间隔，重置尝试次数为1
                user.setEnabled("1");//取消用户锁定
            }else{
                user.setEnabled("0");//未超过时间间隔，锁定用户
                //spring security登录时会根据这个字段自动判断账户是否有效
            }
        }else{
            user.setCounts(user.getCounts()+1);//尝试次数+1
        }
        //更新登录状态
        user.setLasttime(currentTime);//更新最后一次登录失败的时间
        userMapper.updateLoginStatus(user);
        return user.getEnabled();
    }

    //登录成功后，重置登录次数和登录时间
    public void LoginSuccess(String userName) {
        User user = userMapper.selectLoginStatus(userName);
        user.setCounts(0);
        user.setLasttime(new Date());
        userMapper.updateLoginStatus(user);
    }
//    @Autowired
//    private PasswordEncoder encoder;
//    @Autowired
//    UserDetailsService userDetailsService;
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
