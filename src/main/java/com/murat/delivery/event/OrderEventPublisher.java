package com.murat.delivery.event;

import com.murat.delivery.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderShippedEvent(OrderShippedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, event);
        System.out.println("Event Published: " + event);
    }

    public void publishOrderCompletedEvent(Object event) {
        // Sending to a specific queue for AI training
        rabbitTemplate.convertAndSend("ai.training.data", event);
        System.out.println("AI Training Event Published: " + event);
    }
}
