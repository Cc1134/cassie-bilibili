package com.cassie.bilibili.service;

import com.cassie.bilibili.dao.UserFollowingDao;
import com.cassie.bilibili.domain.FollowingGroup;
import com.cassie.bilibili.domain.User;
import com.cassie.bilibili.domain.UserFollowing;
import com.cassie.bilibili.domain.UserInfo;
import com.cassie.bilibili.domain.constant.UserConstant;
import com.cassie.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFollowingService {

    //完成依赖注入
    @Autowired
    private UserFollowingDao userFollowingDao;

    //引入followingGroupService，就可以用45行getByType()方法
    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    //这里涉及先删除再添加的更新操作，所以最好加一个事物处理
    //防止例如我的删除成功了但是新增报错了，这样呢想恢复就没办法恢复了，因为数据已经被删掉了，没办法回滚
    //所以加一个@Transactional事物处理来保证失败的时候可以进行一个回滚
    @Transactional
    public void addUserFollowings(UserFollowing userFollowing){
        //首先来获取分组id：在添加用户的时候需要给用户指定一个搜索分组 - 两类：默认的三种类型 和 用户自定义
        //首次在用户添加关注时，系统需要我们把他添加到默认分组，之后可以进行转移
        //因此先判断用户的id有没有传
        Long groupId = userFollowing.getGroupId();
        if(groupId == null){
            //添加到默认分组
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setFollowingId(followingGroup.getId());
        }else{
            //如果id已经存在了
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if(followingGroup == null){
                throw new ConditionException("关注分组不存在！");
            }
        }
        //添加时间的逻辑
        //先看关注的人是否存在，如果不存在那么添加到数据库就是多了一条垃圾数据
        Long followingId = userFollowing.getFollowingId();
        //@Autowired 引入userService，用user的功能来查询id是否存在
        //getUserById()这个方法本来是不存在的，因为是基于service层面去调用的，但是我们只在dao里写了相关的方法，所以要在service层也新建一个方法
        //为什么不直接@Autowired 引入userDao 然后在userDao里getUserById？
        //这是一个不好的开放习惯：按照架构细分，应该是service和service之间交互，service再用对应的dao进行交互
        //这样的话在dao更新之后，对于其他的service我是无感的，因为我只关注service本身实现的功能
        //两个service之间只关心调用的方法，不关心方法具体的变化
        User user = userService.getUserById(followingId);
        if(user == null){
            throw new ConditionException("关注用户不存在！");
        }
        //关联关系添加
        //做关联关系更新的时候首先可以删除相关的关联关系，然后再进行一个新增
        //根据userId和followingId去数据库删除之前他们之间相关的数据，然后进行重新添加，这样就实现了一个更新功能了
        //而且也可以覆盖更新的情况了，就不需要再单独写更新的判断逻辑了
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);
        //关联关系添加
        userFollowing.setCreateTime(new Date());
        //注意如果不想写更新的逻辑，一定要用先删除再添加的方法进行更新操作来确保流程没有问题
        userFollowingDao.addUserFollowing(userFollowing);
    }

    //第一步：获取关注的用户列表
    //第二步：根据关注用户的id查询关注用户的基本信息 - 因为在设置数据库的时候，只保存了关联的id，并没有把实际的用户信息都放进去
    //第三步：将关注用户按关注分组进行分类 - 方便前端做一个数据展示
    public List<FollowingGroup> getUserFollowings(Long userId){
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if(followingIdSet.size() > 0){
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
        for(UserFollowing userFollowing : list){
            for(UserInfo userInfo : userInfoList){
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
        for(FollowingGroup group : groupList){
            List<UserInfo> infoList = new ArrayList<>();
            for(UserFollowing userFollowing : list){
                if(group.getId().equals(userFollowing.getGroupId())){
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }
        return result;
    }

}
