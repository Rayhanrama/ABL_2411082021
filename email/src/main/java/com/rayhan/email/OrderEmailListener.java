package com.rayhan.email;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderEmailListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEmailListener.class);

    private static final String EVENT_CREATE = "CREATE";

    @Autowired
    private EmailService emailService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    private final RestTemplate restTemplate = new RestTemplate();

    private volatile String cachedToken;

    @RabbitListener(queues = "${app.rabbit.order-email-queue:order.email}")
    public void handleOrderEvent(OrderNotification event) {
        if (event == null || event.getOrderId() == null) {
            return;
        }

        if (!EVENT_CREATE.equals(event.getType())) {
            return;
        }

        log.info("[LISTENER] Menerima event {} untuk order #{}", event.getType(), event.getOrderId());

        OrderNotification order = toOrder(event);
        String token = getAdminToken();
        OrderNotification.Produk produk = fetchProduk(event.getProduk_id(), token);
        OrderNotification.Pelanggan pelanggan = fetchPelanggan(event.getPelanggan_id(), token);

        emailService.sendOrderNotification(order, produk, pelanggan);
    }

    private OrderNotification toOrder(OrderNotification event) {
        OrderNotification order = new OrderNotification();
        order.setId(event.getOrderId());
        order.setProduk_id(event.getProduk_id());
        order.setPelanggan_id(event.getPelanggan_id());
        order.setTgl_transaksi(event.getTgl_transaksi());
        order.setJumlah(event.getJumlah());
        order.setTotal(event.getTotal());
        return order;
    }

    private String getAdminToken() {
        if (cachedToken != null) {
            return cachedToken;
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("AUTH");
        if (instances.isEmpty()) {
            log.warn("[LISTENER] Service AUTH tidak ditemukan di Eureka");
            return null;
        }
        try {
            String url = instances.get(0).getUri() + "/api/auth/login";
            Map<String, String> body = Map.of(
                    "username", adminUsername,
                    "password", adminPassword);
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, body, Map.class);
            if (response != null && response.get("token") != null) {
                cachedToken = "Bearer " + response.get("token");
                log.info("[LISTENER] Token admin berhasil diperoleh");
                return cachedToken;
            }
            log.warn("[LISTENER] Login admin gagal: response tanpa token");
        } catch (Exception e) {
            log.warn("[LISTENER] Gagal login admin ke AUTH: {}", e.getMessage());
        }
        return null;
    }

    private OrderNotification.Produk fetchProduk(Long produkId, String token) {
        return get("PRODUK", "/api/produk/" + produkId, token, OrderNotification.Produk.class);
    }

    private OrderNotification.Pelanggan fetchPelanggan(Long pelangganId, String token) {
        return get("PELANGGAN", "/api/pelanggan/" + pelangganId, token, OrderNotification.Pelanggan.class);
    }

    private <T> T get(String serviceName, String path, String token, Class<T> type) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.isEmpty()) {
            log.warn("[LISTENER] Service {} tidak ditemukan di Eureka", serviceName);
            return null;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            if (token != null) {
                headers.set(HttpHeaders.AUTHORIZATION, token);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            return restTemplate.exchange(instances.get(0).getUri() + path, HttpMethod.GET, entity, type).getBody();
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("401")) {
                cachedToken = null;
            }
            log.warn("[LISTENER] Gagal mengambil {}{}: {}", serviceName, path, e.getMessage());
            return null;
        }
    }
}
