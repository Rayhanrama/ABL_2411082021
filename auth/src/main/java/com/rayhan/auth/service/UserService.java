package com.rayhan.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rayhan.auth.dto.UserResponse;
import com.rayhan.auth.entity.Role;
import com.rayhan.auth.entity.User;
import com.rayhan.auth.repository.UserRepository;

/**
 * Pengelolaan user oleh ADMIN: menampilkan semua user,
 * mengubah role (pelanggan <-> admin) dan status aktif/nonaktif.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse updateRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        Role role;
        try {
            role = Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role tidak valid: " + roleName);
        }

        user.setRole(role);
        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse updateStatus(Long id, boolean aktif) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        user.setAktif(aktif);
        return UserResponse.from(userRepository.save(user));
    }
}
