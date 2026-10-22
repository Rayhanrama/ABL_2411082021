package com.rayhan.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * READ MODEL (sisi Query pada CQRS).
 * Tabel terpisah dari write model {@link Order} (tabel "orders").
 * Data di tabel ini merupakan proyeksi/sinkronisasi dari write model.
 *
 * Kolom "order_id" menyimpan id order dari write model, sedangkan "id"
 * adalah primary key milik tabel read model ini sendiri.
 */
@Entity
@Table(name = "order_view")
public class OrderView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    private Long produk_id;
    private Long pelanggan_id;
    private String tgl_transaksi;
    private int jumlah;
    private double total;

    public OrderView() {
    }

    /** Konversi dari write model ke read model. */
    public static OrderView from(Order order) {
        OrderView view = new OrderView();
        view.setOrderId(order.getId());
        view.setProduk_id(order.getProduk_id());
        view.setPelanggan_id(order.getPelanggan_id());
        view.setTgl_transaksi(order.getTgl_transaksi());
        view.setJumlah(order.getJumlah());
        view.setTotal(order.getTotal());
        return view;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
