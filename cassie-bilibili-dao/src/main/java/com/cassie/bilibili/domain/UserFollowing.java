package com.cassie.bilibili.domain;

import java.util.Date;

public class UserFollowing {
    private Long id;

    private Long userId;

    //关注的用户的id
    private Long followingId;

    private Long groupId;

    //这个表不需要updateTime字段，因为这个用户在添加一个关注的时候是在表里新生成了一个数据，
    // 如果想更新关注关系，只需要把原来的数据删除掉，然后新建一条数据，所以理论上不需要用到updateTime这个字段
    //开发的时候不一定会套模版(数据库里不一定都有createTime和updateTime，可以根据实现逻辑的不同，把表里的有一些字段剔除掉
    private Date createTime;

    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
