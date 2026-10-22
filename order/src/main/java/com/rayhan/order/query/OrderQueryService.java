package com.rayhan.order.query;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.rayhan.order.entity.Order;
import com.rayhan.order.service.OrderService;
import com.rayhan.order.vo.Pelanggan;
import com.rayhan.order.vo.Produk;
import com.rayhan.order.vo.ResponseTemplate;

@Service
public class OrderQueryService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    public List<Order> getAllOrder() {
        return orderService.getAllOrder();
    }

    public Order getOrderById(Long id) {
        return orderService.getOrderById(id);
    }

    public List<ResponseTemplate> getOrderWithProdukById(Long id) {
        List<ResponseTemplate> responseList = new ArrayList<>();

        Order order = getOrderById(id);
        if (order == null) {
            return responseList;
        }

        Produk produk = fetchProduk(order.getProduk_id());
        Pelanggan pelanggan = fetchPelanggan(order.getPelanggan_id());

        ResponseTemplate vo = new ResponseTemplate();
        vo.setOrder(order);
        vo.setProduk(produk);
        vo.setPelanggan(pelanggan);
        responseList.add(vo);
        return responseList;
    }

    private Produk fetchProduk(Long produkId) {
        if (produkId == null) {
            return null;
        }
        ServiceInstance instance = firstInstance("PRODUK");
        if (instance == null) {
            return null;
        }
        try {
            return restTemplate.getForObject(
                    instance.getUri() + "/api/produk/" + produkId, Produk.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Pelanggan fetchPelanggan(Long pelangganId) {
        if (pelangganId == null) {
            return null;
        }
        ServiceInstance instance = firstInstance("PELANGGAN");
        if (instance == null) {
            return null;
        }
        try {
            return restTemplate.getForObject(
                    instance.getUri() + "/api/pelanggan/" + pelangganId, Pelanggan.class);
        } catch (Exception e) {
            return null;
        }
    }

    private ServiceInstance firstInstance(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        return instances.isEmpty() ? null : instances.get(0);
    }
}
