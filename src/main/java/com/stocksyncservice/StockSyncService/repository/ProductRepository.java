package com.stocksyncservice.StockSyncService.repository;

import com.stocksyncservice.StockSyncService.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySKUAndVendor(String sku, String vendor);
}

