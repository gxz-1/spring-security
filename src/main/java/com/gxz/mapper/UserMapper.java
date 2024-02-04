package com.gxz.mapper;


import com.gxz.pojo.MyUser;

public interface UserMapper {

    int updateLoginStatus(MyUser myUser);

    MyUser selectLoginStatus(String username);
}
