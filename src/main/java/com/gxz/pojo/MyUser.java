package com.gxz.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class MyUser {
    String username;
    String password;
    String enabled;
    Integer counts;
    Date lasttime;
}
