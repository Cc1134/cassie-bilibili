package com.cassie.bilibili.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cassie.bilibili.domain.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

//专门用来生成用户令牌
public class TokenUtil {
    //用户令牌主要是用来标识用户身份
    //之所以用令牌是因为直接传用户id可能会有数据不安全的问题
    //引入JWT依赖包里的功能

    //默认签发者：可以写自己所属机构/个人
    private static final String ISSUER = "签发者";
    //创建JWT（用户令牌）的方法
    //写成static方法可以直接被调用
    public static String generateToken(Long userId) throws Exception{
        //JWT需要一个加密算法
        //使用RSA加密因为之前已经有一个RSAUtil已经提前生成了一个密钥对（公钥、私钥），所以直接使用RSA加密就省去了自己再新生成一个密钥的步骤
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
        //用来后续生成过期时间：JWT的过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //超时的时间30秒：生成之后过了30秒这个token就过期了，就不能再使用了
        calendar.add(Calendar.SECOND,30);
        return JWT.create().withKeyId(String.valueOf(userId))
                //JWT的签发者
                .withIssuer(ISSUER)
                //过期时间
                .withExpiresAt(calendar.getTime())
                //生成签名：做整体加密
                .sign(algorithm);
    }

    //验证JWT的方法
    public static Long verifyToken(String token){
        //为什么不能抛出异常只能用try...catch?
        //在使用verifyToken()方法的时候是用在各种需要验证用户身份的地方，而验证有几种情况
        //1.通过了，是合法token
        //2.token过期了：这种情况不能直接抛出异常给前端。如果抛的是通用异常，前端会直接做一个错误码的展示，就无法做进一步操作
            //进一步操作：如果令牌过期，需要以特殊形式给前端返回特殊的状态码和提示消息来告诉他这个token是过期的，这时根据系统需要，
            // 前端就可以手动的刷新token，而不是把错误提示直接给到前端用户，
            // 这是一个基础的用户体验把控，要做到不让用户感知到token过期，而继续快乐的使用我们的系统
        try{
            //创建的时候进行了RSA加密，所以验证的时候需要RSA解密
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
            //JWT需要用这个RSA的算法来生成一个相关的验证类
            JWTVerifier verifier = JWT.require(algorithm).build();
            //直接用这个验证类对JWT进行验证即可，方法是引入的jwt包里直接封装好的，返回的jwt就和未加密之前的jwt是一样的了
            DecodedJWT jwt = verifier.verify(token);
            //这样就可以通过未加密的jwt获取userid了
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
            //抛出过期异常
        }catch (TokenExpiredException e){
            throw new ConditionException("555","token过期！");
        }catch (Exception e){
            throw new ConditionException("非法的用户token！");
        }

    }

}
