package com.rayhan.order.service;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rayhan.order.entity.Order;
import com.rayhan.order.event.OrderEvent;
import com.rayhan.order.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.order-email-queue:order.email}")
    private String orderEmailQueue;

    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order saveOrder(Order order) {
        Order saved = orderRepository.save(order);
        publish(OrderEvent.CREATE, saved);
        return saved;
    }

    public Order updateOrder(Long id, Order order) {
        Order existingOrder = orderRepository.findById(id).orElse(null);
        if (existingOrder != null) {
            existingOrder.setProduk_id(order.getProduk_id());
            existingOrder.setPelanggan_id(order.getPelanggan_id());
            existingOrder.setTgl_transaksi(order.getTgl_transaksi());
            existingOrder.setJumlah(order.getJumlah());
            existingOrder.setTotal(order.getTotal());

            Order saved = orderRepository.save(existingOrder);
            publish(OrderEvent.UPDATE, saved);
            return saved;
        }
        return null;
    }

    public void deleteOrder(Long id) {
        Order existingOrder = orderRepository.findById(id).orElse(null);
        orderRepository.deleteById(id);
        if (existingOrder != null) {
            publish(OrderEvent.DELETE, existingOrder);
        }
    }

    private void publish(String type, Order order) {
        OrderEvent event = new OrderEvent(
                type,
                order.getId(),
                order.getProduk_id(),
                order.getPelanggan_id(),
                order.getTgl_transaksi(),
                order.getJumlah(),
                order.getTotal());

        rabbitTemplate.convertAndSend(orderEmailQueue, event);
    }
}
