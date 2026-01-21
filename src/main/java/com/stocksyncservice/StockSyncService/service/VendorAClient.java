package com.stocksyncservice.StockSyncService.service;

import com.stocksyncservice.StockSyncService.dto.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@Service
public class VendorAClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<ProductDTO> fetchProducts()
    {
        ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity("http://localhost:8080/vendor-a/products", ProductDTO[].class);
        return Arrays.asList(response.getBody());
    }
}
