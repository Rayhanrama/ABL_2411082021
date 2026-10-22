package com.rayhan.email.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representasi pelanggan yang diambil dari service PELANGGAN via Eureka.
 * Field "email" dipakai sebagai alamat tujuan notifikasi.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pelanggan {

    private Long id;

    private String nama;

    private String alamat;

    private String jenis_kelamin;

    private String email;

    public Pelanggan() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
