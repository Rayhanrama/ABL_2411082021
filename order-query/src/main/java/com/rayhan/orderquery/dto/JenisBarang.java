package com.rayhan.orderquery.dto;

public class JenisBarang {
   

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
