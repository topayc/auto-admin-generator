package com.databasemeta.ahn.util;





import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.databasemeta.ahn.dto.TableMetadata;

import java.io.*;
import java.util.*;

public class ExcelExporter {

    public static File exportToExcel(Map<String, TableMetadata> metadata) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();

        for (Map.Entry<String, TableMetadata> entry : metadata.entrySet()) {
            String table = entry.getKey();
            TableMetadata meta = entry.getValue();

            Sheet sheet = wb.createSheet(table);

            int rowIdx = 0;

            rowIdx = writeSection(sheet, "columninfo", meta.columnInfos, rowIdx);
            rowIdx = writeSection(sheet, "index", meta.indexKeys, rowIdx);
            rowIdx = writeSection(sheet, "primary", meta.primaryKeys, rowIdx);
            rowIdx = writeSection(sheet, "foreinkey", meta.foreignKey, rowIdx);
            rowIdx = writeSection(sheet, "referenceme", meta.referenceMes, rowIdx);
        }

        File tempFile = File.createTempFile("metadata-", ".xlsx");
        try (FileOutputStream os = new FileOutputStream(tempFile)) {
            wb.write(os);
        }
        return tempFile;
    }

    private static int writeSection(Sheet sheet, String title, List<?> data, int startRow) {
        Row titleRow = sheet.createRow(startRow++);
        titleRow.createCell(0).setCellValue("===" + title.toUpperCase() + "===");

        if (data == null || data.isEmpty()) return startRow + 1;

        // field names
        List<String> fields = new ArrayList<>();
        for (var f : data.get(0).getClass().getFields()) {
            fields.add(f.getName());
        }

        Row header = sheet.createRow(startRow++);
        for (int i = 0; i < fields.size(); i++) {
            header.createCell(i).setCellValue(fields.get(i));
        }

        for (Object item : data) {
            Row row = sheet.createRow(startRow++);
            for (int i = 0; i < fields.size(); i++) {
                try {
                    Object value = item.getClass().getField(fields.get(i)).get(item);
                    if (value != null) {
                        row.createCell(i).setCellValue(value.toString());
                    }
                } catch (Exception ignored) {}
            }
        }

        return startRow + 2; // space before next section
    }
}
