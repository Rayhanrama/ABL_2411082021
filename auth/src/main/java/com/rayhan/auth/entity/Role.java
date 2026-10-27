package com.rayhan.auth.entity;

/**
 * Peran (role) yang tersedia di sistem.
 * ADMIN     : dapat mengelola produk, seluruh order, dan hak akses user.
 * PELANGGAN : dapat melihat produk, mengelola profil & order miliknya sendiri.
 */
public enum Role {
    ADMIN,
    PELANGGAN
}
