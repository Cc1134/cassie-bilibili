package com.cassie.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper//当遇到interface的时候，框架（mybatis/hibernate）自动读取xml文件，用反射的方法做出了一个class implement 这个interface
public interface DemoDao {
    //新建一个查询方法
    public Long query(Long id);
}
