package com.rayhan.produk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rayhan.produk.entity.Produk;
import com.rayhan.produk.repository.ProdukRepository;

@Service
public class ProdukService {
    @Autowired
    private ProdukRepository produkRepository;

    public List<Produk> getAllProduk() {
        return produkRepository.findAll();
    }

    public Produk saveProduk(Produk produk) {
        return produkRepository.save(produk);
    }

    public Produk getProdukById(Long id) {
        return produkRepository.findById(id).orElse(null);
    }

    public void deleteProduk(Long id) {
        produkRepository.deleteById(id);
    }

    public Produk updateProduk(Long id, Produk produk) {
        Produk existingProduk = produkRepository.findById(id).orElse(null);
        if (existingProduk != null) {
            existingProduk.setNama(produk.getNama());
            existingProduk.setDeskripsi(produk.getDeskripsi());
            existingProduk.setHarga(produk.getHarga());
            return produkRepository.save(existingProduk);
        }
        return null;
    }

       public List<Produk> getProdukByJenis(Long idJenis) {
        return produkRepository.findByIdJenis(idJenis);
    }
}