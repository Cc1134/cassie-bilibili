package com.cassie.bilibili.domain.exception;

//根据条件来抛出异常
public class ConditionException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private String code;//映对到jsonResponse里的code，响应的状态码

    public ConditionException(String code, String name){
        super(name);//引用上一级的构造方法
        this.code = code;
    }


    public ConditionException(String name){
        super(name);//引用上一级的构造方法
        code = "500";//一般"500"代表一个错误的返回状态码，在企业级项目开发中，对于一些常规的错误处理，都可以用"500"来返回给前端
                    // 如果有一些特殊的状态和错误可以用"500"之外的错误状态码例如"501""502"
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
