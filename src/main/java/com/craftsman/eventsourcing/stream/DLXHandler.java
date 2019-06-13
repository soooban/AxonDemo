package com.craftsman.eventsourcing.stream;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class DLXHandler implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    private static final String DLQ = "contract-error.dlq";
    private final RabbitTemplate rabbitTemplate;
    private ApplicationContext applicationContext;

    @Autowired
    public DLXHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // SCS 会创建一个 child context ，这里需要判断下正确的 context 初始化完成
        if (event.getApplicationContext().equals(this.applicationContext)) {
            // 启动后获取 dlq 中所有的消息，进行消费
            Message message = rabbitTemplate.receive(DLQ);
            while (message != null) {
                rabbitTemplate.send(message.getMessageProperties().getReceivedRoutingKey(), message);
                message = rabbitTemplate.receive(DLQ);
            }
        }

    }
}
