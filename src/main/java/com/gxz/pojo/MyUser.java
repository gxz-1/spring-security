package com.gxz.pojo;

import lombok.Data;

import java.util.Date;

//自己实现登录失败多次锁定用户的功能
//根据这个类自己写mapper
@Data
public class MyUser {
    String username;
    String password;
    String enabled;
    Integer counts;
    Date lasttime;
}
