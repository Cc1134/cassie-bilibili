package com.cassie.bilibili.api;

import com.cassie.bilibili.service.DemoService;
import com.sun.javafx.collections.MappingChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName: DemoApi
 * @Description: todo
 * @Author Cassie Chen
 * @Date 2/20/22 9:18 pm
 * @Version 1.0
 */
@RestController
public class DemoApi {

    @Autowired
    private DemoService demoService;

    @GetMapping("/query")//当来自互联网的http的get请， 通过这个注解，spring把请求传递给22行
    public Long query(Long id){
        return demoService.query(id);
    }

}

