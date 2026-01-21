package com.stocksyncservice.StockSyncService.service;

import com.stocksyncservice.StockSyncService.dto.ProductDTO;
import com.stocksyncservice.StockSyncService.entity.Product;
import com.stocksyncservice.StockSyncService.entity.StockEvent;
import com.stocksyncservice.StockSyncService.repository.ProductRepository;
import com.stocksyncservice.StockSyncService.repository.StockEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockSyncService {

    private final VendorAClient vendorAClient;
    private final VendorBCSVReader vendorBCsvReader;
    private final ProductRepository productRepository;
    private final StockEventRepository stockEventRepository;

    @Scheduled(fixedDelay = 60000) //60 seconds
    @Transactional
    public void sync() {

        log.info("Stock Sync Started");
        syncVendor("VENDOR_A", vendorAClient.fetchProducts());
        syncVendor("VENDOR_B", vendorBCsvReader.readProducts());
        log.info("Stock Sync Finished");
    }

    private void syncVendor(String vendor, List<ProductDTO> products) {
        for (ProductDTO dto : products) {

            Product existing =
                    productRepository.findBySkuAndVendor(dto.sku(), vendor)
                            .orElse(null);

            if (existing != null &&
                existing.getStockQuantity() > 0 &&
                dto.stockQuantity() == 0)
            {

                //for updating db out of stock
                stockEventRepository.save(
                        new StockEvent(
                                null,
                                dto.sku(),
                                vendor,
                                LocalDateTime.now(),
                                "Product went out of stock"
                        )
                );

                //for out of stock logs
                log.warn("OUT OF STOCK: {} ({})", dto.sku(), vendor);
            }

            Product product = existing != null ? existing : new Product();
            product.setSku(dto.sku());
            product.setName(dto.name());
            product.setStockQuantity(dto.stockQuantity());
            product.setVendor(vendor);

            productRepository.save(product);
        }
    }
}
