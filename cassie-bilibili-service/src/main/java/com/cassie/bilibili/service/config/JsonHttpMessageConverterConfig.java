package com.cassie.bilibili.service.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ListSerializer;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

//Config表示一个配置文件
@Configuration
public class JsonHttpMessageConverterConfig {

    public static void main(String[] args){

        List<Object> list = new ArrayList<>();
        Object o = new Object();
        list.add(o);
        list.add(o);
        System.out.println(list.size());//打印大小、长度
        System.out.println(JSONObject.toJSONString(list));//打印里面的元素，把json的类转换成符合json格式的字符串
        System.out.println(JSONObject.toJSONString(list,SerializerFeature.DisableCircularReferenceDetect));//关闭循环引用检测
        //output：2
        //[{},{"$ref":"$[0]"}] 第一个是一个json的格式，第二个：当没有关闭循环检测的时候打印出来的数据，里面多出来的字符串是因为
                                //在做json数据构造的时候发现前面已经有相关的object了，后面引用的就是这个objec，
                                // 于是就把引用地址或者引用内容相关的地址拿到括号里写进去了，但这并不是一个我们想要的结果
                                // 实际上就算重复添加，在给前端返回的时候应该是展现相同元素和相同内容展现两次
                                // 所以要关闭循环引用，就会得到下面那种结果
        //[{},{}]
    }

    @Bean
    @Primary//表示注入类是一个比较高的优先级
    //HttpMessageConverters框架里用到的对http方法或者对请求数据做转换的工具类
    public HttpMessageConverters fastJsonHttpMessageConvertes(){
        //继承HttpMessageConverters的相关类
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //FastJson配置相关的类
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        //配置返回数据的时间格式
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        //序列化相关配置
        fastJsonConfig.setSerializerFeatures(
                //json数据需要格式化输出：非格式化的情况下输出可能是没有缩进的或者缩进有问题，这样可以按照标准的json格式进行输出，包括缩进和换行
                SerializerFeature.PrettyFormat,
                //如果json输出的字段是nul，没有值或者不存在，这样系统会直接把这个数据去掉，
                // 如果在返回的结果里看不到这个字段，对前端来说不灵活，即使没有值，前端也可能需要看到这个字段
                //这个注解就把value为null的字段转换成了空字符串，这样就能返回给前端看到
                SerializerFeature.WriteNullStringAsEmpty,
                //如果是没有数据的列表就转成一个空的字符串
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteMapNullValue,
                //对map相关对字段（key和键值对）进行排序，默认升序
                SerializerFeature.MapSortField,
                //禁用循环引用
                //循环引用在数据转换里是一个很重要的概念，见上面例子
                SerializerFeature.DisableCircularReferenceDetect
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastConverter);
    }
}
