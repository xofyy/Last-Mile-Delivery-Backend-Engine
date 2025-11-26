package com.murat.delivery.event;

import com.murat.delivery.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleOrderShippedEvent(OrderShippedEvent event) {
        System.out.println("Notification Service Received Event: " + event);
        System.out.println("Sending email to: " + event.getUserEmail());
        // Simulate email sending logic
    }
}
