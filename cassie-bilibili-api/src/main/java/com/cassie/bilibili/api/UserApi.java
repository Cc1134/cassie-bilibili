package com.cassie.bilibili.api;

import com.cassie.bilibili.domain.JsonResponse;
import com.cassie.bilibili.service.UserService;
import com.cassie.bilibili.service.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//此注解表示这是一个restful风格的控制器
@RestController
public class UserApi {

    //此注解在springboot中用来引入相关依赖或相关实体类的方法
    //把刚才新建的UserService通过注解的形式引入
    //但是这里会有一些问题：稍后的课程会讲解
    @Autowired
    private UserService userService;

    //此接口用来获取rsa公钥，pks就是publi，结合restful命名规范：名词复数形式+横杠连接
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey(){
        //这个方法没有参数因为PublicKey是直接存储在后端的，具体放在了RSAUtil里面 见 PUBLIC_KEY
        //同时在RSAUtil的方法里也提供了一个getPublicKeyStr()的方法来获取公钥
        //所以这里简单的调用一下RSAUtil就可以完成这个接口的功能
        String pk = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(pk);
    }
}
