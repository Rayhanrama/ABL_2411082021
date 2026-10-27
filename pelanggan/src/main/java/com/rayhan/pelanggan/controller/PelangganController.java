package com.rayhan.pelanggan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rayhan.pelanggan.entity.Pelanggan;
import com.rayhan.pelanggan.security.SecurityUtils;
import com.rayhan.pelanggan.service.PelangganService;

@RestController
@RequestMapping("/api/pelanggan")
public class PelangganController {

    @Autowired
    PelangganService pelangganService;

    @Autowired
    SecurityUtils securityUtils;

    /** Admin melihat semua pelanggan; pelanggan hanya melihat datanya sendiri. */
    @GetMapping
    public List<Pelanggan> getAllPelanggan(@AuthenticationPrincipal Jwt jwt) {
        if (securityUtils.isAdmin(jwt)) {
            return pelangganService.getAllPelanggan();
        }
        Pelanggan sendiri = pelangganService.getPelangganByUserId(securityUtils.getUserId(jwt));
        return sendiri == null ? List.of() : List.of(sendiri);
    }

    /** Profil milik user yang sedang login (dipakai service order untuk validasi kepemilikan). */
    @GetMapping("/me")
    public ResponseEntity<Pelanggan> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        Pelanggan pelanggan = pelangganService.getPelangganByUserId(securityUtils.getUserId(jwt));
        if (pelanggan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pelanggan);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelanggan> getPelangganById(@PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Pelanggan pelanggan = pelangganService.getPelangganById(id);
        if (pelanggan == null) {
            return ResponseEntity.notFound().build();
        }
        checkOwnership(jwt, pelanggan);
        return ResponseEntity.ok(pelanggan);
    }

    /** Membuat profil. userId selalu diambil dari token, bukan dari body. */
    @PostMapping
    public ResponseEntity<Pelanggan> createPelanggan(
            @RequestBody Pelanggan pelanggan,
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = securityUtils.getUserId(jwt);
        if (!securityUtils.isAdmin(jwt) && pelangganService.getPelangganByUserId(userId) != null) {
            throw new IllegalArgumentException("Anda sudah memiliki profil pelanggan");
        }
        pelanggan.setUserId(userId);
        return ResponseEntity.ok(pelangganService.savePelanggan(pelanggan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelanggan> updatePelanggan(@PathVariable Long id,
            @RequestBody Pelanggan pelanggan,
            @AuthenticationPrincipal Jwt jwt) {

        Pelanggan existing = pelangganService.getPelangganById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        checkOwnership(jwt, existing);
        return ResponseEntity.ok(pelangganService.updatePelanggan(id, pelanggan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePelanggan(@PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Pelanggan existing = pelangganService.getPelangganById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        checkOwnership(jwt, existing);
        pelangganService.deletePelanggan(id);
        return ResponseEntity.noContent().build();
    }

    /** ADMIN bebas; selain itu userId pada token harus sama dengan pemilik data. */
    private void checkOwnership(Jwt jwt, Pelanggan pelanggan) {
        if (securityUtils.isAdmin(jwt)) {
            return;
        }
        Long userId = securityUtils.getUserId(jwt);
        if (userId == null || !userId.equals(pelanggan.getUserId())) {
            throw new AccessDeniedException("Bukan pemilik data pelanggan ini");
        }
    }
}
