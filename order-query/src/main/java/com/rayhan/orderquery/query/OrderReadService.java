package com.rayhan.orderquery.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.rayhan.orderquery.dto.OrderDto;
import com.rayhan.orderquery.dto.Pelanggan;
import com.rayhan.orderquery.dto.Produk;
import com.rayhan.orderquery.dto.ResponseTemplate;

/**
 * Read-side CQRS: mengambil data dari service lain (ORDER write-side,
 * PRODUK, PELANGGAN) melalui service discovery lalu mengagregasinya.
 * Read-side TIDAK memiliki database sendiri.
 */
@Service
public class OrderReadService {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    /** Mengambil seluruh order mentah dari write-side. */
    public List<OrderDto> getAllRawOrders(String bearerToken) {
        ServiceInstance instance = firstInstance("ORDER");
        if (instance == null) {
            return List.of();
        }
        try {
            HttpEntity<Void> entity = bearerEntity(bearerToken);
            OrderDto[] orders = restTemplate.exchange(
                    instance.getUri() + "/api/order", HttpMethod.GET, entity, OrderDto[].class).getBody();
            return orders == null ? List.of() : Arrays.asList(orders);
        } catch (Exception e) {
            return List.of();
        }
    }

    /** Order mentah berdasarkan id. */
    public OrderDto getRawOrder(Long id, String bearerToken) {
        ServiceInstance instance = firstInstance("ORDER");
        if (instance == null) {
            return null;
        }
        try {
            HttpEntity<Void> entity = bearerEntity(bearerToken);
            ResponseEntity<OrderDto> response = restTemplate.exchange(
                    instance.getUri() + "/api/order/" + id + "/raw", HttpMethod.GET, entity, OrderDto.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                return null;
            }
            return response.getBody();
        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            if (extractStatus(e) == 403) {
                throw new AccessDeniedException("Bukan pemilik order ini");
            }
            return null;
        }
    }

    /** Mengambil status HTTP dari exception web bila tersedia. */
    private int extractStatus(Exception e) {
        if (e instanceof RestClientResponseException rce) {
            return rce.getStatusCode().value();
        }
        if (e instanceof HttpClientErrorException hce) {
            return hce.getStatusCode().value();
        }
        return -1;
    }

    /**
     * Agregasi order + produk + pelanggan. Mengembalikan list agar
     * kompatibel dengan endpoint lama.
     */
    public List<ResponseTemplate> getOrderWithDetails(Long id, String bearerToken) {
        List<ResponseTemplate> responseList = new ArrayList<>();

        OrderDto order = getRawOrder(id, bearerToken);
        if (order == null) {
            return responseList;
        }

        Produk produk = fetchProduk(order.getProduk_id(), bearerToken);
        Pelanggan pelanggan = fetchPelanggan(order.getPelanggan_id(), bearerToken);

        responseList.add(new ResponseTemplate(order, produk, pelanggan));
        return responseList;
    }

    private Produk fetchProduk(Long produkId, String bearerToken) {
        if (produkId == null) {
            return null;
        }
        ServiceInstance instance = firstInstance("PRODUK");
        if (instance == null) {
            return null;
        }
        try {
            return restTemplate.exchange(
                    instance.getUri() + "/api/produk/" + produkId, HttpMethod.GET,
                    bearerEntity(bearerToken), Produk.class).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private Pelanggan fetchPelanggan(Long pelangganId, String bearerToken) {
        if (pelangganId == null) {
            return null;
        }
        ServiceInstance instance = firstInstance("PELANGGAN");
        if (instance == null) {
            return null;
        }
        try {
            return restTemplate.exchange(
                    instance.getUri() + "/api/pelanggan/" + pelangganId, HttpMethod.GET,
                    bearerEntity(bearerToken), Pelanggan.class).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private HttpEntity<Void> bearerEntity(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        if (bearerToken != null) {
            headers.set(HttpHeaders.AUTHORIZATION, bearerToken);
        }
        return new HttpEntity<>(headers);
    }

    private ServiceInstance firstInstance(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        return instances.isEmpty() ? null : instances.get(0);
    }
}
