package com.rayhan.email.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurasi RabbitMQ untuk service email.
 * Hanya mendengarkan queue "order.email" yang dipublikasikan oleh service order.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbit.order-email-queue:order.email}")
    private String orderEmailQueue;

    @Bean
    public Queue orderEmailQueue() {
        return new Queue(orderEmailQueue, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
