package com.rayhan.pelanggan.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity 
public class Pelanggan {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String nama;

    private String alamat;

    private String jenis_kelamin;

    private String email;

    public Pelanggan(){

    }

    public Pelanggan(Long id, String nama, String alamat, String jenis_kelamin, String email){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.jenis_kelamin = jenis_kelamin;
        this.email = email;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getNama(){
        return nama;
    }

    public void setNama(String nama){
        this.nama = nama;
    }

    public String getAlamat(){
        return alamat;
    }

    public void setAlamat(String alamat){
        this.alamat = alamat;
    }

    public String getJenis_kelamin(){
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin){
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

}
