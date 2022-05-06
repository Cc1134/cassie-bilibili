package com.cassie.bilibili.api.support;

import com.cassie.bilibili.domain.exception.ConditionException;
import com.cassie.bilibili.service.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
/*
support用来支撑运行的模块，相关的功能都可以放在这
比如：
一个前置方法，在所有地方都能调用token，使用verifyToken(),来获取我们想要的userId

@Component这个注解方便我们把userSupport在项目构建的时候以依赖的形式注入
如何使用：在UserApi中引进
@Autowired
    private UserSupport userSupport;
 */


@Component
public class UserSupport {

    //用来获取当前用户id
    public Long getCurrentUserId(){
        /*首先对前端传来的请求进行抓取
        "RequestContextHolder" 这个类是springboot 提供的用来抓取上下文的相关方法，可以通过这些方法来获取需要的请求封装好的信息
        拿到这个请求就是为了获取token
        一般来说，token传给前端之后，前端会记在本地的local storage里，下一次前端在请求接口的时候，就会从local storage里拿到token
        把它(一般是)放在请求头里面，这样一来，所有和用户相关的或是所有的接口统一全放在请求头里面，就不用区分接口具体要传入哪些参数了
        这样我的接口就可以写一个通用的方法，也就是这个getCurrentUserId()
        通用方法的功效就是从请求头里统一的拿前端传来的token(用户令牌)进行一个解析
        解析的方法就是TokenUtil里的verifyToken(String token)
         */
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //接着就获取token（用户令牌）
        String token = requestAttributes.getRequest().getHeader("token");
        //如果没有出错，就正常的拿到用户id了，否则就会抛出verifyToken()里预设好的系统异常
        Long userId = TokenUtil.verifyToken(token);
        //id都是从0自增的，所以如果出现负数应该抛出异常
        if(userId < 0){
            throw new ConditionException("非法用户！");
        }
        return userId;
    }
}
