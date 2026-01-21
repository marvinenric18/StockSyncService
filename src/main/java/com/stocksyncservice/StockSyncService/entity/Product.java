package com.stocksyncservice.StockSyncService.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(columnNames = {"sku", "vendor"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sku;
    private String name;
    private Integer stockQuantity;
    private String vendor;
}