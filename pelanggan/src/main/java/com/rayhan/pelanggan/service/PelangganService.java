package com.rayhan.pelanggan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rayhan.pelanggan.entity.Pelanggan;
import com.rayhan.pelanggan.repository.PelangganRepository;


@Service 
public class PelangganService {
    @Autowired 
    private PelangganRepository pelangganRepository;

    public List<Pelanggan> getAllPelanggan(){
        return pelangganRepository.findAll();
    }

    public Pelanggan savePelanggan(Pelanggan pelanggan){
        return pelangganRepository.save(pelanggan);
    }

    public Pelanggan getPelangganById(Long id){
        return pelangganRepository.findById(id).orElse(null);
    }

    public Pelanggan getPelangganByUserId(Long userId){
        return pelangganRepository.findByUserId(userId).orElse(null);
    }

    public void deletePelanggan(Long id){
        pelangganRepository.deleteById(id);
    }

    
    public Pelanggan updatePelanggan(Long id, Pelanggan pelanggan) {
        Pelanggan existingPelanggan = pelangganRepository.findById(id).orElse(null);
        if (existingPelanggan != null) {
            existingPelanggan.setNama(pelanggan.getNama());
            existingPelanggan.setAlamat(pelanggan.getAlamat());
            existingPelanggan.setJenis_kelamin(pelanggan.getJenis_kelamin());
            existingPelanggan.setEmail(pelanggan.getEmail());
            return pelangganRepository.save(existingPelanggan);
        }
        return null;
    }


}
