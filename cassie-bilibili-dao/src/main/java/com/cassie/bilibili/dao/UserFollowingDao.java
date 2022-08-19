package com.cassie.bilibili.dao;

import com.cassie.bilibili.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFollowingDao {
                                //xml里面有两个参数，所以不在xml里指定数据类型，在dao的方法里指定
                                //mybatis 可以根据相关的注解自动识别相关字段的名称
    Integer deleteUserFollowing(@Param("userId") Long userId, @Param("followingId") Long followingId);

    Integer addUserFollowing(UserFollowing userFollowing);

    List<UserFollowing> getUserFollowings(Long userId);

    List<UserFollowing> getUserFans(Long userId);
}
