package com.cassie.bilibili.domain;

//Json返回
public class JsonResponse<T> {

    //返回的状态码
    private String code;

    //返回的提示语
    private String msg;

    //定义一个范型，因为返回的数据类型多种多样，用范型更加灵活
    private T data;

    //自定义构造方法，使使用更灵活
    public JsonResponse(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    //自定义构造方法，使使用更灵活
    public JsonResponse(T data){
        this.data = data;
        msg = "成功";
        code = "0";//非0都是失败
    }

    //请求成功
    //使用场景：一些不需要返回给前端但是请求成功的场景，返回的数据就是一个"成功"的信息和"0"的状态码
    public static JsonResponse<String> success(){
        return new JsonResponse<>(null);//什么都不传，默认为方法成功了
    }

    //上面这种场景的补充，需要给前端返回参数
    //使用场景：用户登录成功之后，系统会给用户返回一个字符串类型的令，这时候就可以调用这个方法直接把令牌作为data传递进来返回给前端
    public static JsonResponse<String> success(String data){
        return new JsonResponse<>(data);
    }

    public static JsonResponse<String> fail(){
        return new JsonResponse<>("1","失败");
    }

    //用于需要返回特定的提示语和code给前端的情况
    public static JsonResponse<String> fail(String code, String msg){
        return new JsonResponse<>(code,msg);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
