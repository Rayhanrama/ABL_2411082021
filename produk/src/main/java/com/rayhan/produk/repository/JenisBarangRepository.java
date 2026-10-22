package com.rayhan.produk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rayhan.produk.entity.JenisBarang;

@Repository
public interface JenisBarangRepository extends JpaRepository<JenisBarang, Long> {

}
