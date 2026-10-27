package com.rayhan.orderquery.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.rayhan.orderquery.dto.Pelanggan;

/**
 * Mengambil profil pelanggan milik pemanggil (endpoint /api/pelanggan/me)
 * untuk keperluan penyaringan data berdasarkan kepemilikan.
 */
@Service
public class PelangganLookupService {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    public Pelanggan fetchMyPelanggan(String bearerToken) {
        ServiceInstance instance = firstInstance("PELANGGAN");
        if (instance == null) {
            return null;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            if (bearerToken != null) {
                headers.set(HttpHeaders.AUTHORIZATION, bearerToken);
            }
            return restTemplate.exchange(
                    instance.getUri() + "/api/pelanggan/me", HttpMethod.GET,
                    new HttpEntity<>(headers), Pelanggan.class).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private ServiceInstance firstInstance(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        return instances.isEmpty() ? null : instances.get(0);
    }
}
