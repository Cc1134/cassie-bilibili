package com.cassie.bilibili.service;

import com.cassie.bilibili.dao.UserDao;
import com.cassie.bilibili.domain.User;
import com.cassie.bilibili.domain.UserInfo;
import com.cassie.bilibili.domain.constant.UserConstant;
import com.cassie.bilibili.domain.exception.ConditionException;
import com.cassie.bilibili.service.util.MD5Util;
import com.cassie.bilibili.service.util.RSAUtil;
import com.cassie.bilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

//此注解的意思就是这个类是服务层的一个类，
// 同时@Service也会在springboot构建的时候，系统会自动把userservice给注入进去，
// 这样的话就可以在实际的阶段直接调用了，就不需要自己再去生成了
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void addUser(User user) {
        //先获取手机号
        String phone = user.getPhone();
        //判断1：手机号是否合法
        if(StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空！");
        }
        //调用下面getUserByPhone(String phone)的方法，查出来的数据就是datebase里的user
        User dbUser = this.getUserByPhone(phone);
        //判断2：手机号的用户是否已经注册 所以要新建一个方法通过手机号去获取用户getUserByPhone(String phone)
        //现在看从数据库里调出来的用户是否已经存在，如果已经注册过就不能再注册了，所以要抛出异常，返回前端一个提示语
        if(dbUser != null){
            throw new ConditionException("该手机号已经注册");
        }
        //注册逻辑从这里开始
        //获取时间戳用来对用户的密码进行md5加密
        //通过时间戳生成一个盐值来配合md5
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        //获取前端传来的密码，这是被前端进行rsa加密之后传过来的，所以首先需要对密码进行解密
        String password = user.getPassword();
        String rawPassword;
        try{
            //rsa解密操作
            rawPassword = RSAUtil.decrypt(password);
        }catch (Exception e){
            throw new ConditionException("密码解密失败！");
        }
        //对解密后的原始密码进行md5加密，传入原始密码，盐值，和字符集"UTF-8"。整个项目都会使用"UTF-8"编码集
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        //现在万事俱备就差往数据库插入了
        //所以利用前端传来的user进行实际赋值
        user.setSalt(salt);
        //md5加密之后的psw
        user.setPassword(md5Password);
        user.setCreateTime(now);
        //调用userDao里addUser(user)的方法把user传进去
        //在userDao里添加addUser(user)的方法
        userDao.addUser(user);
        //创建好用户之后要把用户id拿出来，再创建根据id得到的相关用户信息
        //如果拿到ID？xml里16行useGeneratedKeys="true" keyProperty="id"
        //添加用户信息，构建userinfo的实体类
        UserInfo userInfo = new UserInfo();
        //对实体类进行赋值，获取userid
        userInfo.setUserId(user.getId());
        //基本信息例如系统预设的信息
        //在初次注册的时候，用户只需要填写一个昵称，和手机号，所以有些相关数据是空的
        //所以这里把一些需要系统预设的值拎出来
        //在user实体类里没有昵称，这里可以生成一个系统默认的字段
        //不推荐写死，推荐在service或dao里新建一个包来存储默认值
        //这样做的好处：一旦方法多了，如果setNick这个方法在各种文件里都有调用到，写死的话在需要修改的时候就需要修改很多地方
        //但如果统一集中在userconstant里，涉及到修改的时候只需在userconstant里进行修改即可
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        //在userDao添加userInfo实体类到数据库里
        userDao.addUserInfo(userInfo);

    }

    //通过手机号去获取用户，Dao里也有同名方法
    public User getUserByPhone(String phone){
        return userDao.getUserByPhone(phone);
    }


    public String login(User user) throws Exception{
        //先拿到手机号
        String phone = user.getPhone();
        //判断手机号
        if(StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空！");
        }
        User dbUser = this.getUserByPhone(phone);
        //注册是判断这个用户是不能存在的，登录时判断用户必须存在
        if(dbUser == null){
            throw new ConditionException("当前用户不存在！");
        }
        //判断用户密码是否匹配
        String password = user.getPassword();
        String rawPassword;
        try{
            //解密密码
            rawPassword = RSAUtil.decrypt(password);
        }catch (Exception e){
            throw new ConditionException("密码解密失败！");
        }
        //盐值在注册的时候生成了，现在直接在数据库里获取
        String salt = dbUser.getSalt();
        //然后用数据库里get到的这个salt进行加密，这样就拿到md5加密之后的密码
        String md5Password = MD5Util.sign(rawPassword,salt,"UTF-8");
        //对比生成的md5密码和dbUser数据库里存放的是否一样即可（equals是看内容是否一致，不是看地址值）
        if(!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("解密错误！");
        }
        //走到这一步说明用户已经判断为合法用户，所以只需要生成用户令牌返回给前端就可以了
        //具体生成：使用service.util包下的TokenUtil
        return TokenUtil.generateToken(dbUser.getId());

    }

    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }
}
