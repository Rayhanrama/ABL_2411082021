package com.rayhan.producer;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Service
public class ProducerService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue queue;

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(queue.getName(), message);
        System.out.println("Sent: " + message);
    }
}
