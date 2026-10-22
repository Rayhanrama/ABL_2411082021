package com.rayhan.produk.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class JenisBarang {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJenis;
    private String jenis;

    public JenisBarang(){

    }

    public JenisBarang(Long idJenis, String jenis){
        this.idJenis = idJenis;
        this.jenis = jenis;
    }

    public Long getIdJenis(){
        return idJenis;
    }

    public void setIdJenis(Long idJenis){
        this.idJenis = idJenis;
    }

    public String getJenis(){
        return jenis;
    }

    public void setJenis(String jenis){
        this.jenis = jenis;
    }

}
