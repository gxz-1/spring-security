package com.gxz.service;

import com.gxz.mapper.UserMapper;
import com.gxz.pojo.MyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class LoginFailureService {
    @Autowired
    private UserMapper userMapper;

    //在一定时间内登录失败超过一定次数，锁定用户
    public String LoginFailure(String userName, Integer lockCounts, Long lockDuration){
        MyUser myUser = userMapper.selectLoginStatus(userName);
        Date currentTime = new Date();
        if(myUser.getCounts()>=lockCounts){
            if( currentTime.getTime() - myUser.getLasttime().getTime()>=lockDuration){
                myUser.setCounts(1);//超过时间间隔，重置尝试次数为1
                myUser.setEnabled("1");//取消用户锁定
            }else{
                myUser.setEnabled("0");//未超过时间间隔，锁定用户
                //spring security登录时会根据这个字段自动判断账户是否有效
            }
        }else{
            myUser.setCounts(myUser.getCounts()+1);//尝试次数+1
        }
        //更新登录状态
        myUser.setLasttime(currentTime);//更新最后一次登录失败的时间
        userMapper.updateLoginStatus(myUser);
        return myUser.getEnabled();
    }

    //登录成功后，重置登录次数和登录时间
    public void LoginSuccess(String userName) {
        MyUser myUser = userMapper.selectLoginStatus(userName);
        myUser.setCounts(0);
        myUser.setLasttime(new Date());
        userMapper.updateLoginStatus(myUser);
    }
}
