package com.cassie.bilibili.api;

import com.cassie.bilibili.api.support.UserSupport;
import com.cassie.bilibili.domain.JsonResponse;
import com.cassie.bilibili.domain.PageResult;
import com.cassie.bilibili.domain.User;
import com.cassie.bilibili.domain.UserInfo;
import com.cassie.bilibili.service.UserService;
import com.cassie.bilibili.service.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

//此注解表示这是一个restful风格的控制器
@RestController
public class UserApi {

    //此注解在springboot中用来引入相关依赖或相关实体类的方法
    //把刚才新建的UserService通过注解的形式引入
    //但是这里会有一些问题：稍后的课程会讲解
    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    //获取用户信息
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo(  ){
        //具体参数就可以不通过请求体传入了，而是直接从userSupport里获取到，因为有了@Component和@Autowired注解
        Long userId = userSupport.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        //基于token的身份验证，我们倾向于把用户令牌token放在前端的请求头中，
        // 通过一个统一的方法，也就是"usersupport"来从请求头中拿到相关的用户信息
        //这样我的接口在设计上就是一个方法可以复用在多个地方，这样的设计就方便后续开发
        return new JsonResponse<>(user);
    }

    //此接口用来获取rsa公钥，pks就是public，结合restful命名规范：名词复数形式+横杠连接
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey(){
        //这个方法没有参数因为PublicKey是直接存储在后端的，具体放在了RSAUtil里面 见 PUBLIC_KEY
        //同时在RSAUtil的方法里也提供了一个getPublicKeyStr()的方法来获取公钥
        //所以这里简单的调用一下RSAUtil就可以完成这个接口的功能
        String pk = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(pk);
    }

    //post本身可以用于用户注册相关的接口功能
    //用@PostMapping来标识我们的接口是一个post请求，接口名叫users，语义就是新建一个用户
    @PostMapping("/users")
    //实体方法 注解@RequestBody是把User在前端传入的时候进行封装，封装成一个json类型然后提供给我们
    public JsonResponse<String> addUser(@RequestBody User user){
        userService.addUser(user);
        //成功创建用户之后就返回给前端提示成功的信息
        //为什么不需要判断失败还是成功而是直接返回成功？
        // 因为会在adduser里提前把各种失败的原因写好，而且在失败的瞬间会立刻抛出异常
        //所以正常来说如果能走到最后一步，这个方法是不会有什么错误的，所以直接调用JsonResponse.success()
        return JsonResponse.success();
    }

    //用户登录接口-逻辑：根据前端传过来的信息判断用户是不是合理的，用户密码是否正确，
    // 如果正确，就把相关的用户凭证返回给前端
    //名称：user-tokens。为什么用user-tokens？登录成功后获取到一个用户凭证/用户令牌（user-tokens）
    //所以做这个登录实际是请求用户令牌这个资源
    @PostMapping("/user-tokens")
    //参数@RequestBody User user里有用户名和密码
    public JsonResponse<String> login(@RequestBody User user) throws Exception{
        //在userService里新建login()方法 返回凭证/令牌
        String token = userService.login(user);
        return new JsonResponse<>(token);
    }

    @PutMapping("/users")
    public JsonResponse<String> updateUsers(@RequestBody User user) throws Exception{
        Long userId = userSupport.getCurrentUserId();
        user.setId(userId);
        userService.updateUsers(user);
        return JsonResponse.success();
    }

    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfos(@RequestBody UserInfo userInfo){
        //涉及到用户相关的功能：userId一般都是从token里面获取到的，而不是前端传给我们的
        //如果前端直接传给我们，可能会被拦截，然后仿造一个相同用户id或者其他用户i，被攻击者来利用而获取到用户的相关信息
        //所以userId通常从token来获取，因为token一般没有办法轻易伪造，就算被拦截，因为token有一个有效期，过了有效期，这个攻击者就不能使用了
        //所以以后获取userId大部分是使用userSupport.getCurrentUserId()这个方法
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfos(userInfo);
        return JsonResponse.success();
    }

    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>> pageResultJsonResponse(@RequestParam Integer no, @RequestParam Integer size, String nick){
        Long userId = userSupport.getCurrentUserId();

    }

}
