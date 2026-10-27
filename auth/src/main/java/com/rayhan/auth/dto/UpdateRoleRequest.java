package com.rayhan.auth.dto;

/**
 * Permintaan admin untuk mengubah role user.
 */
public class UpdateRoleRequest {

    private String role;

    public UpdateRoleRequest() {
    }

    public UpdateRoleRequest(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
