package com.cassie.bilibili.service.config;

import com.cassie.bilibili.domain.constant.UserConstant;
import com.cassie.bilibili.domain.constant.UserMomentsConstant;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

//RocketMQ有几个重要的概念：name server名称服务器 & broker代理服务器 - 这俩之间有一个合作关系
//RocketMQ百分之七八十的工作都是由代理服务器完成的，这里可以配置一下broker和name server

@Configuration
public class RocketMQConfig {
    //配置方式有很多种，这里写最简单的本地变量
    //private final String nameServeAddr = "localhost:937";
    //或者用另一种引入变量的方式，@Value 也是Springboot提供的一种引入变量的方式
            //这个自定义属性的名称应该配置在application-test.properties里
    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    //用户动态提醒需要使用rocketm，也就是mq和redis搭配使用，所以在mq的配置当中需要再引入一个redis template-也就是redis的一个工具类的工具
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //接下来引入两个关键：生产者和消费者
    //rocketmq里有两个和消息相关的角色：消息的生产者和消费者
    //这里主要是实现用户动态提醒，所以生产者和消费者都跟用户动态相关功能进行统一命名
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);//常量引入，在dao包下面新建一个常量类
        //给producer设置名称服务器的地址
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    //生成消费者
    //DefaultMQPushConsumer-在介绍订阅发布模式的时候有提到，消费者去拉取信息的方式有两种：第一种是直接由代理人推送给consumer，这就是push(推送)的方式
    //还有一种是consumer按需去找中间人获取相关数据，这种是拉取的方式，这里默认是push(推送)的方式
    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(nameServerAddr);
        //第三步：订阅消费模式：消费者要订阅生产者，这里还需要订阅的操作；引入订阅的内容(一个常量)
        //除了subscription还有一个subExpression的次级主题
        //如果使用"*"就表示跟这个主题所有相关的次级分类下面的内容都要订阅
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS,"*");

        //第四步：给消费者添加一个监听器-当生产者把消息推送到mq的时候，mq会把相关的消息推给消费者，消费者需要使用监听器来抓取消息并进行下一步操作
        //以动态提醒为例：我关注的up主新建来一条动态发送到了mq, mq马上通知我，我使用监听器发现up主更新东西了，
        // 然后我需要把信息整理起来跟其他我关注的up主放在一个列表里，这样进行前段页面访问时就可以统一的看见这些消息了
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println(msg);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }


}
