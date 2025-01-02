package com.craftly.parser.service;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CSVGenerationService {

    public String generateCSV(List<Map<String, String>> data, String fileName) throws IOException {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Data list is empty. Cannot generate CSV.");
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] headers = data.get(0).keySet().toArray(new String[0]);
            writer.writeNext(headers);

            data.forEach(row -> writer.writeNext(
                    Arrays.stream(headers).toList().stream()
                            .map(header -> row.getOrDefault(header, ""))
                            .toArray(String[]::new)
            ));
        }

        return fileName;
    }
}
