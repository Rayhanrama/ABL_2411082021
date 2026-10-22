package com.rayhan.produk.service;

import java.util.List;


import org.springframework.stereotype.Service;

import com.rayhan.produk.entity.JenisBarang;
import com.rayhan.produk.repository.JenisBarangRepository;

@Service
public class JenisBarangService {
     private final JenisBarangRepository jenisBarangRepository;

    public JenisBarangService(JenisBarangRepository jenisBarangRepository) {
        this.jenisBarangRepository = jenisBarangRepository;
    }

    public List<JenisBarang> getAllJenis() {
        return jenisBarangRepository.findAll();
    }

    public JenisBarang saveJenis(JenisBarang jenisBarang) {
        return jenisBarangRepository.save(jenisBarang);
    }
}
