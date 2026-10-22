package com.rayhan.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rayhan.order.command.OrderCommandService;
import com.rayhan.order.entity.Order;
import com.rayhan.order.entity.OrderView;
import com.rayhan.order.query.OrderQueryService;
import com.rayhan.order.repository.OrderRepository;
import com.rayhan.order.vo.ResponseTemplate;

/**
 * Controller yang memisahkan jalur Command (write) dan Query (read) sesuai pola CQRS.
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    // --- COMMAND SIDE (Write) ---
    @Autowired
    private OrderCommandService orderCommandService;

    // --- QUERY SIDE (Read) ---
    @Autowired
    private OrderQueryService orderQueryService;

    // Dipakai untuk endpoint internal (mis. oleh service email).
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public List<OrderView> getAllOrder() {
        return orderQueryService.getAllOrder();
    }

    @GetMapping("/{id}")
    public List<ResponseTemplate> getOrderWithEntityById(@PathVariable Long id) {
        return orderQueryService.getOrderWithProdukById(id);
    }

    /**
     * Endpoint internal: mengembalikan entitas order (write model) apa adanya.
     * Digunakan service email untuk mengambil data order via Eureka.
     */
    @GetMapping("/{id}/raw")
    public ResponseEntity<Order> getRawOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderCommandService.createOrder(order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        Order updated = orderCommandService.updateOrder(id, order);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderCommandService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
