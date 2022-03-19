package com.cassie.bilibili.api;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RESTfulApi {

    private final Map<Integer, Map<String, Object>> dataMap;

    //initialize dataMap
    public RESTfulApi(){
        dataMap = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            Map<String,Object> data = new HashMap<>();
            data.put("id", i);
            data.put("name", "name"+i);
            dataMap.put(i, data);
            
        }
    }

    @GetMapping("/objects/{id}")
                                        //这个注解用于把id和 @GetMapping 里的id进行关联
    public Map<String, Object> getData(@PathVariable Integer id){
        return dataMap.get(id);
    }

    @DeleteMapping("/objects/{id}")
    public String deleteData(@PathVariable Integer id){
        dataMap.remove(id);
        return "delete sucess";
    }

    @PostMapping("/objects")
                            //作用：Springboot会根据这个注解把data variable进行自动封装，然后以JSON的形式传输进来，
                            // 这样就很符合restful风格的设计了
                            //json，由javascript语法衍生而来的文本传输格式，在spring端自动解析成java数据对象，
                            // 以便你的函数直接访问对象而不是手工解析文本格式
    public String postData(@RequestBody Map<String, Object> data){
        //先获得data里所有key的集合，才知道新建的key应该是第几个了
        //java泛型语法的先天缺陷导致toArray函数不得不接收一个长度为0的数组，才能做到返回数组中的元素类型是Integer
        Integer[] idArray = dataMap.keySet().toArray(new Integer[0]);
        Arrays.sort(idArray);
        int nextId = idArray[idArray.length - 1] + 1;
        dataMap.put(nextId, data);
        return "post success";
    }


    @PutMapping("/objects")
    public String putData(@RequestBody Map<String, Object> data){
        Integer id = Integer.valueOf(String.valueOf(data.get("id")));
        Map<String, Object> containedData = dataMap.get(id);
        //课件说POST和PUT的区别容易被简单地误认为“POST表示创建资源，
        // PUT表示更新资源” 而实际上，二者均可用于创建资源，更为本质的差别是在幂等性方面
        //POST所指向资源并非POST要创建的资源本身，而是POST创建资源的接收者。
        // PUT对应的资源是要创建或更新的资源本身，语义是创建或更新，对同一资源进行多次PUT的副作用和一次PUT是相同的，因此，PUT方法具有幂等性
        //在这个例子里，原本的资源不存在，所以只能new一个资源
        if(containedData == null){
            //新增
            Integer[] idArray = dataMap.keySet().toArray(new Integer[0]);
            Arrays.sort(idArray);
            int nextId = idArray[idArray.length - 1] + 1;
            dataMap.put(nextId, data);
        }else{
            //更新
            dataMap.put(id, data);
        }
        return "put success";
    }

}
