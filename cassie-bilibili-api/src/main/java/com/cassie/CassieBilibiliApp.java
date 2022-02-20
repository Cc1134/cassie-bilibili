package com.cassie;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @ClassName: CassieBilibiliApp
 * @Description: todo
 * @Author Cassie Chen
 * @Date 2/20/22 9:51 am
 * @Version 1.0
 */

@SpringBootApplication
public class CassieBilibiliApp {
    //编写入口
    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(CassieBilibiliApp.class, args);
    }
}

