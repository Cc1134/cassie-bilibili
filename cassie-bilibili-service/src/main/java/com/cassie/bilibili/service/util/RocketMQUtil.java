package com.cassie.bilibili.service.util;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

public class RocketMQUtil {
    //首先写一个同步发送信息的方法(有同步发送和异步发送两种方法)
    //发送的返回值也可以进行区分：一种是不给提醒一种是给提醒
    //同步                                                     //之前在RocketMQConfig里提到的MessageExt实际上是继承了Message
    public static void syncSendMsg(DefaultMQProducer producer, Message msg) throws Exception{
        SendResult result = producer.send(msg);
        System.out.println(result);
    }

    //异步
    public static void asyncSendMsg(DefaultMQProducer producer, Message msg) throws Exception{
        //计数器：发送两次
        int messageCount = 2;
        //引入倒计时计数器，为了让大家熟悉异步发送的流程
        //如果业务需求场景对发送消息的回执不是很关心，就可以采用异步发送消息的方式
        //如果很关心业务有没有成功的发送到m，就需要同步发送的方法
        CountDownLatch2 countDownLatch = new CountDownLatch2(messageCount);
        for (int i = 0; i < messageCount; i++) {
            //加一个反馈(发送成功的回调，或者发送失败的提醒)
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    countDownLatch.countDown();//计数器减一次
                    System.out.println(sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    countDownLatch.countDown();
                    System.out.println("发送消息的时候发生了异常！" + e);
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await(5, TimeUnit.SECONDS);
    }
}
