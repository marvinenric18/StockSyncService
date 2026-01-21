
package com.stocksyncservice.StockSyncService.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import com.stocksyncservice.StockSyncService.dto.ProductDTO;

@Service
@RequiredArgsConstructor
public class VendorBCSVReader {

    @Value("${vendor-b.csv-path}")
    private String csvPath;

    public List<ProductDTO> readProducts()
    {
        Path path = Paths.get(csvPath);

        if (!Files.exists(path))
        {
            return List.of();
        }

        try (Reader reader = Files.newBufferedReader(path))
        {
            CSVParser parser = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            List<ProductDTO> products = new ArrayList<>();

            for (CSVRecord record : parser) {
                products.add(new ProductDTO(
                        record.get("sku"),
                        record.get("name"),
                        Integer.parseInt(record.get("stockQuantity"))
                ));
            }

            return products;

        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to read Vendor B CSV", e);
        }
    }
}
