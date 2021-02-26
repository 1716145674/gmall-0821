package com.atguigu.gmall.pms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;

/**
 * @author zqq
 * @create 2021-02-02 18:35
 */
@Configuration
@Slf4j
public class RabbitMqConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        // 配置交换机确认,每次发信息到交换机都会回调一次
        rabbitTemplate.setConfirmCallback((@Nullable CorrelationData var1, boolean var2, @Nullable String var3) -> {

            if (var2) {
                System.out.println("消息发送到了交换机,"+var3);
            } else {
                System.out.println("消息发送到交换机失败,"+var3);
            }
        });
        // 配置mq确认,只有当mq没有接受到才会回调一次
        rabbitTemplate.setReturnCallback((Message var1, int var2, String var3, String var4, String var5)->{
            log.error("消息发送到消息队列失败: 交换机:{},路由key: {} ,信息: {}",var4,var5,new String(var1.getBody()));
        });
    }
}
