package com.cassie.bilibili.service;

import com.cassie.bilibili.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//此注解的意思就是这个类是服务层的一个类，
// 同时@Service也会在springboot构建的时候，系统会自动把userservice给注入进去，
// 这样的话就可以在实际的阶段直接调用了，就不需要自己再去生成了
@Service
public class UserService {

    @Autowired
    private UserDao userDao;
}
