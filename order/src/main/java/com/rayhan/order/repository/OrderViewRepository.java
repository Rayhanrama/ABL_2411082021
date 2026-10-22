package com.rayhan.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rayhan.order.entity.OrderView;

@Repository
public interface OrderViewRepository extends JpaRepository<OrderView, Long> {

    Optional<OrderView> findByOrderId(Long orderId);
}
