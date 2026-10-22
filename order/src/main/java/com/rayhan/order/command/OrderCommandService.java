package com.rayhan.order.command;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rayhan.order.entity.Order;
import com.rayhan.order.event.OrderEvent;
import com.rayhan.order.repository.OrderRepository;

/**
 * COMMAND SIDE (CQRS - Write).
 * Bertanggung jawab terhadap semua operasi yang mengubah state:
 * create, update, delete.
 *
 * Setiap perubahan pada write model (tabel "orders") TIDAK lagi menyentuh
 * read model secara langsung. Perubahan dipublikasikan sebagai {@link OrderEvent}
 * ke RabbitMQ:
 *  - queue "order.sync"  -> dibaca oleh OrderReadModelListener untuk memperbarui
 *                           read model (tabel "order_view").
 *  - queue "order.email" -> dibaca oleh service email untuk mengirim notifikasi.
 */
@Service
public class OrderCommandService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.order-sync-queue:order.sync}")
    private String orderSyncQueue;

    @Value("${app.rabbit.order-email-queue:order.email}")
    private String orderEmailQueue;

    public Order createOrder(Order order) {
        Order saved = orderRepository.save(order);

        // Publikasikan event: sinkronisasi read model + trigger email.
        OrderEvent event = toEvent(OrderEvent.CREATE, saved);
        publish(orderSyncQueue, event);
        publish(orderEmailQueue, event);

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

            // Sinkronisasi read model melalui RabbitMQ.
            publish(orderSyncQueue, toEvent(OrderEvent.UPDATE, saved));

            return saved;
        }
        return null;
    }

    public void deleteOrder(Long id) {
        Order existingOrder = orderRepository.findById(id).orElse(null);
        orderRepository.deleteById(id);

        // Beri tahu read model untuk menghapus baris terkait.
        OrderEvent event = existingOrder != null
                ? toEvent(OrderEvent.DELETE, existingOrder)
                : new OrderEvent(OrderEvent.DELETE, id, null, null, null, 0, 0);
        publish(orderSyncQueue, event);
    }

    private OrderEvent toEvent(String type, Order order) {
        return new OrderEvent(
                type,
                order.getId(),
                order.getProduk_id(),
                order.getPelanggan_id(),
                order.getTgl_transaksi(),
                order.getJumlah(),
                order.getTotal());
    }

    private void publish(String queue, OrderEvent event) {
        rabbitTemplate.convertAndSend(queue, event);
    }
}
