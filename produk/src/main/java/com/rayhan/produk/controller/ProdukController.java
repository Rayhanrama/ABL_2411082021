package com.rayhan.produk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rayhan.produk.entity.JenisBarang;
import com.rayhan.produk.entity.Produk;
import com.rayhan.produk.service.JenisBarangService;
import com.rayhan.produk.service.ProdukService;

@RestController
@RequestMapping("/api/produk")
public class ProdukController {

    @Autowired
    ProdukService produkService;
    @Autowired
    JenisBarangService jenisBarangService;


    @GetMapping
    public List<Produk> getAllProduk() {
        return produkService.getAllProduk();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produk> getProdukById(
            @PathVariable Long id) {

        Produk produk = produkService.getProdukById(id);

        if (produk == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(produk);
    }

    @PostMapping
    public ResponseEntity<Produk> createProduk(
            @RequestBody Produk produk) {

        return ResponseEntity.ok(
                produkService.saveProduk(produk));
    }

    @GetMapping("/jenis")
    public List<JenisBarang> getAllJenis() {
        return jenisBarangService.getAllJenis();
    }

    @PostMapping("/jenis")
    public JenisBarang createJenis(
            @RequestBody JenisBarang jenisBarang) {

        return jenisBarangService.saveJenis(jenisBarang);
    }

    @GetMapping("/jenis/{idJenis}")
    public List<Produk> getProdukByJenis(
            @PathVariable Long idJenis) {

        return produkService.getProdukByJenis(idJenis);
    }
}