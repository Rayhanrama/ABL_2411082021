package com.rayhan.produk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rayhan.produk.entity.Produk;

@Repository
public interface ProdukRepository extends JpaRepository<Produk, Long> {
    List<Produk> findByIdJenis(Long idJenis);
}
