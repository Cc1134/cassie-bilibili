package com.cassie.bilibili.dao;

import com.cassie.bilibili.domain.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//在service内部调用dao
@Mapper
public interface FollowingGroupDao {

    //这两个方法主要是在添加用户关注的时候，针对于用户关注分组进行查询
    //然后需要对应的xml文件写对应的sql语句
    FollowingGroup getByType(String type);

    FollowingGroup getById(Long id);

    List<FollowingGroup> getByUserId(Long userId);

    Integer addFollowingGroup(FollowingGroup followingGroup);

    List<FollowingGroup> getUserFollowingGroups(Long userId);
}
