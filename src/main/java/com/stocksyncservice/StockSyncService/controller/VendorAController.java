package com.stocksyncservice.StockSyncService.controller;

import com.stocksyncservice.StockSyncService.dto.ProductDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vendor-a")
public class VendorAController {

    @GetMapping("/products")
    public List<ProductDTO> products() {

        //Creating mock data for Vendor A
        return List.of(
                new ProductDTO("ABC123", "Product A", 8),
                new ProductDTO("LMN789", "Product C", 0)
        );
    }
}
