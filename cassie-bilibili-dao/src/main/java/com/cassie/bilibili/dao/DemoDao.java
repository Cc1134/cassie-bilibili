package com.cassie.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoDao {
    //新建一个查询方法
    public Long query(Long id);
}
