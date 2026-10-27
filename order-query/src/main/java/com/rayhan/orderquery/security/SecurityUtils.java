package com.rayhan.orderquery.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Helper membaca claim userId & role dari JWT.
 */
@Component
public class SecurityUtils {

    public Long getUserId(Jwt jwt) {
        if (jwt == null) {
            return null;
        }
        Object userId = jwt.getClaim("userId");
        if (userId == null) {
            return null;
        }
        return Long.valueOf(userId.toString());
    }

    public String getRole(Jwt jwt) {
        if (jwt == null) {
            return null;
        }
        Object role = jwt.getClaim("role");
        return role == null ? null : role.toString();
    }

    public boolean isAdmin(Jwt jwt) {
        return "ADMIN".equalsIgnoreCase(getRole(jwt));
    }
}
