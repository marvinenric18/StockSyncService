package com.stocksyncservice.StockSyncService.dto;

public record ProductDTO(
        String sku,
        String name,
        Integer stockQuantity
) {}
