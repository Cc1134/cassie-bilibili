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
    /*
    本来是
    @Autowired
    private UserFollowingDao userFollowingDao;
    但是改成下面这样
     */
    //private应该只在内部使用，但是@Autowired违反了这个规定
    //spring在初始化userFollowingDao的时候就
    //serviceInstance.dao = daoInstance @Autowired给这个变量赋值，绑定instance，
    // 但是这个变量是private就不应该被这个文件以外的代码所使用到
    //解决方法：用一个 public setter
    //这样把依赖注入放在一个public setter上，而不是private field
    private UserFollowingDao userFollowingDao;

    @Autowired
    public void setUserFollowingDao(UserFollowingDao userFollowingDao){
        this.userFollowingDao = userFollowingDao;
    }


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
        //groupId不存在的情况
        if(groupId == null){
            //添加到默认分组
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setFollowingId(followingGroup.getId());
        }else{
            //如果groupId已经存在了
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

    //第一步：获取关注的用户列表 <拿到用户的关注信息>
    //第二步：根据关注用户的id查询关注用户的基本信息 - 因为在设置数据库的时候，只保存了关联的id，并没有把实际的用户信息都放进去
    //第三步：将关注用户按关注分组进行分类 - 方便前端做一个数据展示
    public List<FollowingGroup> getUserFollowings(Long userId){
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
        //用lambda表达式把表里的followingId都抽出来
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        //查所有的对应id的参数
        List<UserInfo> userInfoList = new ArrayList<>();
        //前置判断：如果followingIdSet有值才进行操作
        if(followingIdSet.size() > 0){
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
        //拿到用户信息，然后做匹配
        for(UserFollowing userFollowing : list){
            for(UserInfo userInfo : userInfoList){
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        //根据用户id把用户相关的所有关注分组都查出来
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        //都查出来之后进行分组：先添加一个全部关注分组 <前端展示的时候需要的一个分组，不需要存在数据库中，因为这个分组是全部的参数拿出来拼的>
        FollowingGroup allGroup = new FollowingGroup();
        //设置全部关注分组的名字和信息
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        //返回数据
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
        //查到的groupList跟userInfo整合
        for(FollowingGroup group : groupList){
            List<UserInfo> infoList = new ArrayList<>();
            for(UserFollowing userFollowing : list){
                if(group.getId().equals(userFollowing.getGroupId())){
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);//？？？？？？？不是加了两次所有的关注对象么
        }
        return result;
    }

    //获取粉丝列表的相关方法
    //第一步：获取当前用户的粉丝列表 - 获取到的都是id
    //第二步：根据粉丝的用户id查询基本信息
    //第三步：查询当前用户是否已经关注该粉丝 - 把获取到的粉丝列表里的所有粉丝都拎出来，看看这些粉丝有没有都关注过，
    //当前这个登陆用户有没有关注过粉丝列表里的某一个粉丝，如果关注过 - 你是我的粉丝，我是你的粉丝，就应该是一个互粉的状态
    //互粉的状态在前端也要有一个单独的状态表示，所以需要把这个状态查出来告诉前端，这个状态是有变化的
    public List<UserFollowing> getUserFans(Long userId){
        //获取用户粉丝 - getUserFans()方法在dao里新建，然后建立xml sql语句
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
        //然后把粉丝的id都抽取出来
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        //如果有粉丝
        if(fanIdSet.size() > 0){
            //粉丝相关信息查询
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }
        //判断当前粉丝列表里有没有此用户关注过的 - followingList是当前用户已经关注的用户信息
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        //遍历粉丝列表，看哪些是已经关注过的
        for(UserFollowing fan : fanList){
            //用UserInfo赋值
            for(UserInfo userInfo : userInfoList){
                //同步的过程中把互粉的状态也提前在UserInfo里设置好：增加一个boolean 如果是true表示已经关注了，否则就是没有关注
                //所有的用户信息，如果有相关匹配的话，设一个默认的关注状态 false
                if(fan.getUserId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(false);
                    //粉丝用户信息赋值
                    fan.setUserInfo(userInfo);
                }
            }
            //用户关注列表
            for(UserFollowing following : followingList){
                //如果被关注的id正好等于粉丝的userId -> 那就是一个互粉的状态
                if(following.getFollowingId().equals(fan.getUserId())){
                    //设置true -> 互相关注
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }
        return fanList;
    }

    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        followingGroupService.addFollowingGroup(followingGroup);
        return followingGroup.getId();
    }

    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }
}
