package com.stocksyncservice.StockSyncService;

import com.stocksyncservice.StockSyncService.dto.ProductDTO;
import com.stocksyncservice.StockSyncService.entity.Product;
import com.stocksyncservice.StockSyncService.entity.StockEvent;
import com.stocksyncservice.StockSyncService.repository.ProductRepository;
import com.stocksyncservice.StockSyncService.repository.StockEventRepository;
import com.stocksyncservice.StockSyncService.service.StockSyncService;
import com.stocksyncservice.StockSyncService.service.VendorAClient;
import com.stocksyncservice.StockSyncService.service.VendorBCSVReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StockSyncServiceTest {

	private VendorBCSVReader csvReader;

	@Mock
	private VendorBCSVReader vendorBCsvReader;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private StockEventRepository stockEventRepository;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private VendorAClient vendorAClient;

	@InjectMocks
	private StockSyncService stockSyncService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		csvReader = new VendorBCSVReader();
	}

	@Test
	void StockSyncService() {
		// Vendor A: one existing product going out of stock
		ProductDTO vendorAProductOutOfStock = new ProductDTO("SKU_A", "Product A", 0);
		Product existingVendorA = new Product();
		existingVendorA.setSku("SKU_A");
		existingVendorA.setName("Product A");
		existingVendorA.setStockQuantity(5);
		existingVendorA.setVendor("VENDOR_A");

		// Vendor A: one existing product not going out of stock
		ProductDTO vendorAProductStillStock = new ProductDTO("SKU_A2", "Product A2", 10);
		Product existingVendorA2 = new Product();
		existingVendorA2.setSku("SKU_A2");
		existingVendorA2.setName("Product A2");
		existingVendorA2.setStockQuantity(10);
		existingVendorA2.setVendor("VENDOR_A");

		// Vendor B: new product
		ProductDTO vendorBNewProduct = new ProductDTO("SKU_B", "Product B", 15);

		when(vendorAClient.fetchProducts()).thenReturn(List.of(vendorAProductOutOfStock, vendorAProductStillStock));
		when(vendorBCsvReader.readProducts()).thenReturn(List.of(vendorBNewProduct));

		when(productRepository.findBySkuAndVendor("SKU_A", "VENDOR_A")).thenReturn(Optional.of(existingVendorA));
		when(productRepository.findBySkuAndVendor("SKU_A2", "VENDOR_A")).thenReturn(Optional.of(existingVendorA2));
		when(productRepository.findBySkuAndVendor("SKU_B", "VENDOR_B")).thenReturn(Optional.empty());

		// Act
		stockSyncService.sync();

		// Capture saved products
		ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
		verify(productRepository, times(3)).save(productCaptor.capture());
		List<Product> savedProducts = productCaptor.getAllValues();

		// Vendor A out-of-stock
		Product savedVendorA = savedProducts.stream()
				.filter(p -> p.getSku().equals("SKU_A"))
				.findFirst().orElseThrow();
		assertEquals(0, savedVendorA.getStockQuantity());

		// Vendor A still in stock
		Product savedVendorA2 = savedProducts.stream()
				.filter(p -> p.getSku().equals("SKU_A2"))
				.findFirst().orElseThrow();
		assertEquals(10, savedVendorA2.getStockQuantity());

		// Vendor B new product
		Product savedVendorB = savedProducts.stream()
				.filter(p -> p.getSku().equals("SKU_B"))
				.findFirst().orElseThrow();
		assertEquals(15, savedVendorB.getStockQuantity());

		// Capture StockEvent for out-of-stock only
		ArgumentCaptor<StockEvent> stockEventCaptor = ArgumentCaptor.forClass(StockEvent.class);
		verify(stockEventRepository, times(1)).save(stockEventCaptor.capture());
		StockEvent stockEvent = stockEventCaptor.getValue();
		assertEquals("SKU_A", stockEvent.getSku());
		assertEquals("VENDOR_A", stockEvent.getVendor());
		assertEquals("Product went out of stock", stockEvent.getMessage());
	}

	@Test
	void VendorBCSVReaderService(@TempDir Path tempDir) throws IOException {
		// Create a temporary CSV file
		Path csvFile = tempDir.resolve("vendor_b.csv");
		try (BufferedWriter writer = Files.newBufferedWriter(csvFile)) {
			writer.write("sku,name,stockQuantity\n");
			writer.write("SKU1,Product1,10\n");
			writer.write("SKU2,Product2,5\n");
		}

		// Set the csvPath field in VendorBCSVReader
		ReflectionTestUtils.setField(csvReader, "csvPath", csvFile.toString());

		List<ProductDTO> products = csvReader.readProducts();

		assertEquals(2, products.size());

		assertEquals("SKU1", products.get(0).sku());
		assertEquals("Product1", products.get(0).name());
		assertEquals(10, products.get(0).stockQuantity());

		assertEquals("SKU2", products.get(1).sku());
		assertEquals("Product2", products.get(1).name());
		assertEquals(5, products.get(1).stockQuantity());
	}

}
