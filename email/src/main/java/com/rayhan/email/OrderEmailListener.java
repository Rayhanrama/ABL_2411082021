package com.rayhan.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class OrderEmailListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEmailListener.class);

    private static final String EVENT_CREATE = "CREATE";

    @Autowired
    private EmailService emailService;

    @Autowired
    private DiscoveryClient discoveryClient;

    private final RestTemplate restTemplate = new RestTemplate();

    @RabbitListener(queues = "${app.rabbit.order-email-queue:order.email}")
    public void handleOrderEvent(OrderNotification event) {
        if (event == null || event.getId() == null) {
            return;
        }

        log.info("[LISTENER] Menerima event order #{}", event.getId());

        OrderNotification order = fetchOrder(event.getId());
        OrderNotification.Produk produk = fetchProduk(event.getProduk_id());
        OrderNotification.Pelanggan pelanggan = fetchPelanggan(event.getPelanggan_id());

        emailService.sendOrderNotification(order, produk, pelanggan);
    }

    private OrderNotification fetchOrder(Long orderId) {
        return get("ORDER", "/api/order/" + orderId + "/raw", OrderNotification.class);
    }

    private OrderNotification.Produk fetchProduk(Long produkId) {
        return get("PRODUK", "/api/produk/" + produkId, OrderNotification.Produk.class);
    }

    private OrderNotification.Pelanggan fetchPelanggan(Long pelangganId) {
        return get("PELANGGAN", "/api/pelanggan/" + pelangganId, OrderNotification.Pelanggan.class);
    }

    private <T> T get(String serviceName, String path, Class<T> type) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.isEmpty()) {
            log.warn("[LISTENER] Service {} tidak ditemukan di Eureka", serviceName);
            return null;
        }
        try {
            return restTemplate.getForObject(instances.get(0).getUri() + path, type);
        } catch (Exception e) {
            log.warn("[LISTENER] Gagal mengambil {}{}: {}", serviceName, path, e.getMessage());
            return null;
        }
    }
}
