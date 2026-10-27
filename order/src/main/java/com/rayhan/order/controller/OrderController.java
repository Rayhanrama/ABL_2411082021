package com.rayhan.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rayhan.order.entity.Order;
import com.rayhan.order.security.SecurityUtils;
import com.rayhan.order.service.OrderService;
import com.rayhan.order.service.PelangganLookupService;

/**
 * Sisi COMMAND (write) pada CQRS. Menangani create/update/delete order.
 * Pembacaan & agregasi data ada di service terpisah "order-query".
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PelangganLookupService pelangganLookupService;

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * Membuat order. Untuk pelanggan, pelanggan_id dipaksa mengikuti profil
     * miliknya; admin bebas mengisi pelanggan_id.
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String bearerToken) {
        if (!securityUtils.isAdmin(jwt)) {
            Long pelangganId = pelangganLookupService.fetchMyPelangganId(bearerToken);
            if (pelangganId != null) {
                order.setPelanggan_id(pelangganId);
            }
            if (order.getPelanggan_id() == null) {
                throw new IllegalArgumentException(
                        "Anda belum memiliki profil pelanggan. Buat profil terlebih dahulu.");
            }
        }
        return ResponseEntity.ok(orderService.saveOrder(order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String bearerToken) {
        Order existing = orderService.getOrderById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        checkOwnership(jwt, existing, bearerToken);
        // Pelanggan tidak boleh mengalihkan order ke pelanggan lain.
        if (!securityUtils.isAdmin(jwt)) {
            order.setPelanggan_id(existing.getPelanggan_id());
        }
        return ResponseEntity.ok(orderService.updateOrder(id, order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String bearerToken) {
        Order existing = orderService.getOrderById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        checkOwnership(jwt, existing, bearerToken);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Pembacaan mentah (dipakai email-service & order-query) =====

    @GetMapping("/{id}/raw")
    public ResponseEntity<Order> getRawOrderById(@PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String bearerToken) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        checkOwnership(jwt, order, bearerToken);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public List<Order> getAllRawOrder(@AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String bearerToken) {
        if (securityUtils.isAdmin(jwt)) {
            return orderService.getAllOrder();
        }
        Long pelangganId = pelangganLookupService.fetchMyPelangganId(bearerToken);
        return orderService.getAllOrder().stream()
                .filter(o -> pelangganId != null && pelangganId.equals(o.getPelanggan_id()))
                .toList();
    }

    /** ADMIN bebas; selain itu order.pelanggan_id harus milik pelanggan pemanggil. */
    private void checkOwnership(Jwt jwt, Order order, String bearerToken) {
        if (securityUtils.isAdmin(jwt)) {
            return;
        }
        Long pelangganId = pelangganLookupService.fetchMyPelangganId(bearerToken);
        if (pelangganId == null || !pelangganId.equals(order.getPelanggan_id())) {
            throw new AccessDeniedException("Bukan pemilik order ini");
        }
    }
}
