package com.rayhan.order.event;

import java.io.Serializable;

/**
 * Event yang dipublikasikan ke RabbitMQ setiap kali terjadi perubahan order.
 *
 * type: CREATE | UPDATE | DELETE
 */
public class OrderEvent implements Serializable {

    public static final String CREATE = "CREATE";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    private String type;

    private Long orderId;

    private Long produk_id;

    private Long pelanggan_id;

    private String tgl_transaksi;

    private int jumlah;

    private double total;

    public OrderEvent() {
    }

    public OrderEvent(String type, Long orderId, Long produk_id, Long pelanggan_id,
            String tgl_transaksi, int jumlah, double total) {
        this.type = type;
        this.orderId = orderId;
        this.produk_id = produk_id;
        this.pelanggan_id = pelanggan_id;
        this.tgl_transaksi = tgl_transaksi;
        this.jumlah = jumlah;
        this.total = total;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
