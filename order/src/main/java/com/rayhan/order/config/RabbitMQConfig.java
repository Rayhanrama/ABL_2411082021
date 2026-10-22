package com.rayhan.order.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurasi RabbitMQ untuk service order.
 *
 * Dua queue yang dipakai:
 *  - order.sync  : sinkronisasi write model (tabel "orders") ke read model (tabel "order_view").
 *  - order.email : memicu service email untuk mengirim notifikasi order.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbit.order-sync-queue:order.sync}")
    private String orderSyncQueue;

    @Value("${app.rabbit.order-email-queue:order.email}")
    private String orderEmailQueue;

    @Bean
    public Queue orderSyncQueue() {
        return new Queue(orderSyncQueue, false);
    }

    @Bean
    public Queue orderEmailQueue() {
        return new Queue(orderEmailQueue, false);
    }

    /** Serialisasi event sebagai JSON agar bisa dibaca lintas service (order & email). */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
