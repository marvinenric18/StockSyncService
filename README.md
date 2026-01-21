# Stock Sync Service

## Overview
Stock Sync Service is a Spring Boot microservice responsible for synchronizing
product stock levels from multiple vendors into a centralized database.

The service periodically retrieves stock data, normalizes it, persists it,
and detects when a product transitions from having stock to being out of stock.

This project was implemented as part of a senior backend coding challenge,
focusing on clean architecture, maintainability, and real-world integration patterns.

---

## Tech Stack
- Java 17
- Spring Boot 3
- H2 In-Memory Database
- Spring Scheduler
- OpenAPI / Swagger
- Maven

---

## Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.8+
- IntelliJ IDE or any IDE you preferred
- (Optional) Docker

---

### Run Locally

1. **Clone the repository**
   ```
   git clone <repository-url>
   ```
   ```
   cd Stock-Sync-Service
   ```
   
2. **Open IntelliJ IDE or any IDE you preferred**
3. **Open file or Project then select pom.xml**
4. **You have two options to run**
      ```
      option 1:
      run from main class
      a. Navitage main class
      b. Right click the file then select RUN
      ```
      ```
      option 2:
      open terminal
      a. type mvn clean install
      b. type mvn spring-boot:run
      ```
      
---

### How I simulated Vendor A and Vendor B

1. Vendor A (REST API)
Vendor A is simulated using a mock REST endpoint within the application.

* Endpoint: GET /vendor-a/products
* Returns JSON payload with SKU, name, and stock quantity
* Simulates a real external REST integration
```
[
  { "sku": "ABC123", "name": "Product A", "stockQuantity": 8 },
  { "sku": "LMN789", "name": "Product C", "stockQuantity": 0 }
]
```

2. Vendor B (CSV File)
Vendor B is simulated as a batch file integration using CSV File.

* Data source: CSV file on local filesystem
* Path is externalized via configuration
* CSV is re-read on every scheduled sync
CSV Format below
```
sku,name,stockQuantity
ABC123,Product A,10
XYZ456,Product B,0
```

This also have Stock Synchronization Logic.

---

### Assumptions and Trade-offs
Assumptions

* Each synchronization run retrieves a full product list from each vendor
* Vendors are considered authoritative for their own data
* CSV file is UTF-8 encoded and well-formed
* H2 database is sufficient for demonstration purposes

Trade-offs
* Error handling favors resilience and logging over retries/backoff logic
* In-memory database (H2) was chosen for simplicity instead of PostgreSQL/MySQL
* Scheduler uses a single-threaded execution model

---

### Possible Improvements

* Incremental (delta-based) synchronization
* Async processing for large vendor catalogs

--- 
NOTE: CSV path can be configurable in application.yml

You can pull this code from: https://github.com/marvinenric18/StockSyncService.git
