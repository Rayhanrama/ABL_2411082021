package com.rayhan.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Helper write-side untuk memvalidasi kepemilikan order dengan cara
 * mengambil profil pelanggan milik pemanggil (/api/pelanggan/me).
 * Agregasi & pembacaan data (query) ada di service terpisah "order-query".
 */
@Service
public class PelangganLookupService {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    /** Mengembalikan id profil pelanggan milik pemanggil, atau null bila belum ada. */
    public Long fetchMyPelangganId(String bearerToken) {
        ServiceInstance instance = firstInstance("PELANGGAN");
        if (instance == null) {
            return null;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            if (bearerToken != null) {
                headers.set(HttpHeaders.AUTHORIZATION, bearerToken);
            }
            PelangganProfile profile = restTemplate.exchange(
                    instance.getUri() + "/api/pelanggan/me", HttpMethod.GET,
                    new HttpEntity<>(headers), PelangganProfile.class).getBody();
            return profile == null ? null : profile.getId();
        } catch (Exception e) {
            return null;
        }
    }

    private ServiceInstance firstInstance(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        return instances.isEmpty() ? null : instances.get(0);
    }

    /** DTO minimal untuk membaca id profil pelanggan. */
    public static class PelangganProfile {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
