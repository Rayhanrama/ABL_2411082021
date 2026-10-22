package com.rayhan.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long produk_id;
    private Long pelanggan_id;
    private String tgl_transaksi;
    private int jumlah;
    private double total;

    public Order() {

    }

    public Order(Long id, Long produk_id, Long pelanggan_id, String tgl_transaksi, int jumlah, double total) {
        this.id = id;
        this.produk_id = produk_id;
        this.pelanggan_id = pelanggan_id;
        this.tgl_transaksi = tgl_transaksi;
        this.jumlah = jumlah;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProduk_id() {
        return produk_id;
    }

    public void setProduk_id(Long produk_id) {
        this.produk_id = produk_id;
    }

    public Long getPelanggan_id() {
        return pelanggan_id;
    }

    public void setPelanggan_id(Long pelanggan_id) {
        this.pelanggan_id = pelanggan_id;
    }

    public String getTgl_transaksi() {
        return tgl_transaksi;
    }

    public void setTgl_transaksi(String tgl_transaksi) {
        this.tgl_transaksi = tgl_transaksi;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
