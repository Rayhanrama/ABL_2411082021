package com.rayhan.order.vo;

import com.rayhan.order.entity.OrderView;

public class ResponseTemplate {
    OrderView order;
    Produk produk;
    Pelanggan pelanggan;

    public ResponseTemplate() {

    }

    public ResponseTemplate(OrderView order, Produk produk, Pelanggan pelanggan) {
        this.order = order;
        this.produk = produk;
        this.pelanggan = pelanggan;
    }

    public OrderView getOrder() {
        return order;
    }

    public void setOrder(OrderView order) {
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
