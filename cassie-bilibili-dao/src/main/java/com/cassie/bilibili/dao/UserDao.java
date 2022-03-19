package com.cassie.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;

//用来跟数据库进行交互
//@Mapper是mybatis的一个注解
//当一个interface被标注了@Mapper 就可以跟mybatis产生一个关联，然后mybatis就可以把它管理的xml跟相关的实体类进行一个关联
// 那么我在这个实体类里写的方法就会自动的映射到mybatis的xml文件里，这样就行成了一个完成的跟数据库的交互逻辑了
@Mapper
public interface UserDao {
}
