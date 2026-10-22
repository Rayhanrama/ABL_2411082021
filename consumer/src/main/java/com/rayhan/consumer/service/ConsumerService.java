package com.rayhan.consumer.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @RabbitListener(queues = "my-queue")
    public void receiveMessage(String message) {
        System.out.println("Received: " + message);
    }
}
