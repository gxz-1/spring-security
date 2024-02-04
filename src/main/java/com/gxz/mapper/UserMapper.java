package com.gxz.mapper;


import com.gxz.pojo.User;

public interface UserMapper {

    int updateLoginStatus(User user);

    User selectLoginStatus(String username);
}
