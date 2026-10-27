package com.rayhan.orderquery.dto;

/**
 * Hasil agregasi read-side: order + produk + pelanggan.
 */
public class ResponseTemplate {

    private OrderDto order;
    private Produk produk;
    private Pelanggan pelanggan;

    public ResponseTemplate() {
    }

    public ResponseTemplate(OrderDto order, Produk produk, Pelanggan pelanggan) {
        this.order = order;
        this.produk = produk;
        this.pelanggan = pelanggan;
    }

    public OrderDto getOrder() {
        return order;
    }

    public void setOrder(OrderDto order) {
        this.order = order;
    }

    public Produk getProduk() {
        return produk;
    }

    public void setProduk(Produk produk) {
        this.produk = produk;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public void setPelanggan(Pelanggan pelanggan) {
        this.pelanggan = pelanggan;
    }
}
