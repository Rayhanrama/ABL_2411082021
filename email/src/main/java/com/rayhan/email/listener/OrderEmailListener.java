package com.rayhan.email.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.rayhan.email.event.OrderEvent;
import com.rayhan.email.service.EmailService;
import com.rayhan.email.vo.Order;
import com.rayhan.email.vo.Pelanggan;
import com.rayhan.email.vo.Produk;

/**
 * Mendengarkan queue "order.email". Untuk setiap event CREATE, listener ini
 * MEMANGGIL ULANG service ORDER, PRODUK, dan PELANGGAN melalui Eureka
 * (RestTemplate @LoadBalanced berdasarkan spring.application.name service tujuan),
 * lalu meminta EmailService mengirim notifikasi ke email pelanggan.
 */
@Component
public class OrderEmailListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEmailListener.class);

    private static final String EVENT_CREATE = "CREATE";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = "${app.rabbit.order-email-queue:order.email}")
    public void handleOrderEvent(OrderEvent event) {
        if (event == null || event.getType() == null) {
            return;
        }

        // Notifikasi email hanya untuk pembuatan order baru.
        if (!EVENT_CREATE.equals(event.getType())) {
            return;
        }

        log.info("[LISTENER] Menerima event {} untuk order #{}", event.getType(), event.getOrderId());

        Order order = fetchOrder(event.getOrderId());
        Produk produk = fetchProduk(event.getProduk_id());
        Pelanggan pelanggan = fetchPelanggan(event.getPelanggan_id());

        emailService.sendOrderNotification(order, produk, pelanggan);
    }

    private Order fetchOrder(Long orderId) {
        if (orderId == null) {
            return null;
        }
        try {
            return restTemplate.getForObject("http://ORDER/api/order/" + orderId + "/raw", Order.class);
        } catch (Exception e) {
            log.warn("[LISTENER] Gagal mengambil order #{}: {}", orderId, e.getMessage());
            return null;
        }
    }

    private Produk fetchProduk(Long produkId) {
        if (produkId == null) {
            return null;
        }
        try {
            return restTemplate.getForObject("http://PRODUK/api/produk/" + produkId, Produk.class);
        } catch (Exception e) {
            log.warn("[LISTENER] Gagal mengambil produk #{}: {}", produkId, e.getMessage());
            return null;
        }
    }

    private Pelanggan fetchPelanggan(Long pelangganId) {
        if (pelangganId == null) {
            return null;
        }
        try {
            return restTemplate.getForObject("http://PELANGGAN/api/pelanggan/" + pelangganId, Pelanggan.class);
        } catch (Exception e) {
            log.warn("[LISTENER] Gagal mengambil pelanggan #{}: {}", pelangganId, e.getMessage());
            return null;
        }
    }
}
