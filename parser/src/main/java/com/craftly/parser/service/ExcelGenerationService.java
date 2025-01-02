package com.craftly.parser.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
public class ExcelGenerationService {

    public void generateExcel(String filePath, List<Map<String, String>> data) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data list cannot be null or empty");
        }

        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(filePath)) {
            Sheet sheet = workbook.createSheet("Data");
            String[] headers = data.get(0).keySet().toArray(new String[0]);

            Row headerRow = sheet.createRow(0);
            IntStream.range(0, headers.length).forEach(i -> headerRow.createCell(i).setCellValue(headers[i]));

            IntStream.range(0, data.size()).forEach(rowIndex -> {
                Row dataRow = sheet.createRow(rowIndex + 1);
                IntStream.range(0, headers.length).forEach(i -> {
                    String cellValue = data.get(rowIndex).getOrDefault(headers[i], "");
                    dataRow.createCell(i).setCellValue(cellValue);
                });
            });

            workbook.write(fos);

        }
    }

}