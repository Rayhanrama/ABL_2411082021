package com.rayhan.pelanggan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rayhan.pelanggan.entity.Pelanggan;

@Repository
public interface PelangganRepository extends JpaRepository<Pelanggan, Long> {

    Optional<Pelanggan> findByUserId(Long userId);
}
