package com.rayhan.orderquery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Read-side (Query) pada arsitektur CQRS.
 * Bertanggung jawab menampilkan / mengagregasi data order dengan
 * produk & pelanggan. Data order diambil dari write-side (service ORDER).
 */
@SpringBootApplication
@EnableDiscoveryClient
public class OrderQueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderQueryApplication.class, args);
    }
}
