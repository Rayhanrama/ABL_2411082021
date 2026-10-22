package com.rayhan.pelanggan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rayhan.pelanggan.entity.Pelanggan;
import com.rayhan.pelanggan.service.PelangganService;

@RestController 
@RequestMapping ("/api/pelanggan")
public class PelangganController {
    @Autowired 
    PelangganService pelangganService;


    @GetMapping 
    public List<Pelanggan> getAllPelanggan(){
        return pelangganService.getAllPelanggan();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelanggan> getPelangganById(@PathVariable Long id){

        Pelanggan pelanggan = pelangganService.getPelangganById(id);

        if(pelanggan == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pelanggan);
    }

    @PostMapping
    public ResponseEntity<Pelanggan> createPelanggan(
            @RequestBody Pelanggan pelanggan) {

        return ResponseEntity.ok(
                pelangganService.savePelanggan(pelanggan));
    }
}
