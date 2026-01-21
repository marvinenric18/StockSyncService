package com.stocksyncservice.StockSyncService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StockSyncServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockSyncServiceApplication.class, args);
	}

}
