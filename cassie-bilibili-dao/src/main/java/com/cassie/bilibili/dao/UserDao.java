package com.cassie.bilibili.dao;

import com.cassie.bilibili.domain.User;
import com.cassie.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

//用来跟数据库进行交互
//@Mapper是mybatis的一个注解
//当一个interface被标注了@Mapper 就可以跟mybatis产生一个关联，然后mybatis就可以把它管理的xml跟相关的实体类进行一个关联
// 那么我在这个实体类里写的方法就会自动的映射到mybatis的xml文件里，这样就行成了一个完成的跟数据库的交互逻辑了
@Mapper
public interface UserDao {

    //在xml文件里有获取数据的sql方法
    User getUserByPhone(String phone);

    //返回Integer，因为数据库实际插入以后会返回一个整数类型，具体含义就是成功数据的数量
    //然后在xml文件里添加相关的sql语句
    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoByUserId(Long userId);

    Integer updateUsers(User user);

    User getUserByPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);

    Integer updateUserInfos(UserInfo userInfo);


    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList);
}
