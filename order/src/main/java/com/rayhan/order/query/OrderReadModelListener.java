package com.rayhan.order.query;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rayhan.order.entity.OrderView;
import com.rayhan.order.event.OrderEvent;
import com.rayhan.order.repository.OrderViewRepository;

/**
 * QUERY SIDE (CQRS - Read model updater).
 *
 * Listener ini menerima {@link OrderEvent} dari queue "order.sync" dan
 * memperbarui read model (tabel "order_view"). Dengan cara ini write model
 * (tabel "orders") dan read model terpisah sepenuhnya dan hanya terhubung
 * melalui RabbitMQ (eventual consistency).
 */
@Component
public class OrderReadModelListener {

    @Autowired
    private OrderViewRepository orderViewRepository;

    @RabbitListener(queues = "${app.rabbit.order-sync-queue:order.sync}")
    public void handleOrderEvent(OrderEvent event) {
        if (event == null || event.getType() == null) {
            return;
        }

        switch (event.getType()) {
            case OrderEvent.CREATE:
            case OrderEvent.UPDATE:
                upsert(event);
                break;
            case OrderEvent.DELETE:
                delete(event);
                break;
            default:
                break;
        }
    }

    private void upsert(OrderEvent event) {
        OrderView view = orderViewRepository.findByOrderId(event.getOrderId())
                .orElseGet(OrderView::new);

        view.setOrderId(event.getOrderId());
        view.setProduk_id(event.getProduk_id());
        view.setPelanggan_id(event.getPelanggan_id());
        view.setTgl_transaksi(event.getTgl_transaksi());
        view.setJumlah(event.getJumlah());
        view.setTotal(event.getTotal());

        orderViewRepository.save(view);
        System.out.println("[READ-MODEL] " + event.getType() + " order #" + event.getOrderId() + " disinkronkan.");
    }

    private void delete(OrderEvent event) {
        orderViewRepository.findByOrderId(event.getOrderId())
                .ifPresent(orderViewRepository::delete);
        System.out.println("[READ-MODEL] DELETE order #" + event.getOrderId() + " dari read model.");
    }
}
