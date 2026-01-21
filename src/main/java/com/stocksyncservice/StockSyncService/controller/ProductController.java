package com.stocksyncservice.StockSyncService.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.stocksyncservice.StockSyncService.repository.ProductRepository;
import com.stocksyncservice.StockSyncService.entity.Product;

import java.util.List;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository repository;

    @GetMapping
    public List<Product> all() {
        return repository.findAll();
    }
}
