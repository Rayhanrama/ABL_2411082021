package com.rayhan.orderquery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rayhan.orderquery.dto.OrderDto;
import com.rayhan.orderquery.dto.Pelanggan;
import com.rayhan.orderquery.dto.ResponseTemplate;
import com.rayhan.orderquery.query.OrderReadService;
import com.rayhan.orderquery.security.SecurityUtils;
import com.rayhan.orderquery.service.PelangganLookupService;

/**
 * Endpoint baca (CQRS query side). Admin melihat semua; pelanggan
 * hanya melihat order miliknya.
 */
@RestController
@RequestMapping("/api/order-query")
public class OrderQueryController {

    @Autowired
    private OrderReadService orderReadService;

    @Autowired
    private PelangganLookupService pelangganLookupService;

    @Autowired
    private SecurityUtils securityUtils;

    @GetMapping
    public List<OrderDto> getAllOrder(@AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String bearerToken) {

        List<OrderDto> orders = orderReadService.getAllRawOrders(bearerToken);

        if (securityUtils.isAdmin(jwt)) {
            return orders;
        }
        Long pelangganId = currentPelangganId(bearerToken);
        return orders.stream()
                .filter(o -> pelangganId != null && pelangganId.equals(o.getPelanggan_id()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ResponseTemplate>> getOrderWithDetails(@PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String bearerToken) {

        OrderDto order = orderReadService.getRawOrder(id, bearerToken);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        if (!securityUtils.isAdmin(jwt)) {
            Long pelangganId = currentPelangganId(bearerToken);
            if (pelangganId == null || !pelangganId.equals(order.getPelanggan_id())) {
                throw new AccessDeniedException("Bukan pemilik order ini");
            }
        }

        return ResponseEntity.ok(orderReadService.getOrderWithDetails(id, bearerToken));
    }

    private Long currentPelangganId(String bearerToken) {
        Pelanggan myProfile = pelangganLookupService.fetchMyPelanggan(bearerToken);
        return myProfile == null ? null : myProfile.getId();
    }
}
