package com.cassie.bilibili.api;

import com.cassie.bilibili.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/query")
    public Long query(Long id){
        return demoService.query(id);
    }

}

