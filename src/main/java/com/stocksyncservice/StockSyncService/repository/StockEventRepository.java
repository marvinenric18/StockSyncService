package com.stocksyncservice.StockSyncService.repository;

import com.stocksyncservice.StockSyncService.entity.StockEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StockEventRepository extends JpaRepository<StockEvent, UUID> {
}
