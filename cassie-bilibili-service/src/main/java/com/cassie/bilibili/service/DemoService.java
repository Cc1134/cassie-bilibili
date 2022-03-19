package com.cassie.bilibili.service;

import com.cassie.bilibili.dao.DemoDao;
import com.sun.javafx.collections.MappingChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: DemoService
 * @Description: todo
 * @Author Cassie Chen
 * @Date 2/20/22 9:11 pm
 * @Version 1.0
 */

@Service
public class DemoService {

    @Autowired//由框架进行赋值
    private DemoDao demoDao;

    public Long query(Long id){
        return demoDao.query(id);
    }
}

