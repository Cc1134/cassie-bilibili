package com.cassie.bilibili.service.handler;

import com.cassie.bilibili.domain.JsonResponse;
import com.cassie.bilibili.domain.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

//全局异常处理器
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)//全局异常处理器的优先级应该是最高的
public class CommonGlobalExceptionHandler {

    //标识这是一个异常处理器，value表示针对哪一类型的。这里先用一个全局的Exception来标识，也就是说只要是抛出了一个异常都要用这个方法进行处理
    @ExceptionHandler(value = Exception.class)
    //标识我返回的参数是一个ResponseBody
    @ResponseBody
    //方法  HttpServletRequest用来封装获得的请求，一般前端的请求内容：
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e){
        String errorMsg = e.getMessage();
        if(e instanceof ConditionException){
            //ConditionException区别于Exception有一个code字段，所以从这里可以获取到错误的状态码
            String errorCode = ((ConditionException)e).getCode();
            //从而在主动抛出异常的场景：用户登录，如果我在把用户密码和数据库密码进行匹配，发现密码不匹配，这时就可以主动抛出一个异常
            // 异常类型就是ConditionException，抛出这个异常之后就可以直接告诉前端这个密码不匹配然后前端返回的状态码是"500"
            // 前端拿到这个状态码后发现这是一个通用的状态码就可以直接拿错误信息展示给用户
            //这个情况是可以定制化抛出想要的errorMsg
            return new JsonResponse<>(errorCode,errorMsg);
        }else {
            return new JsonResponse<>("500",errorMsg);
        }
    }

}
