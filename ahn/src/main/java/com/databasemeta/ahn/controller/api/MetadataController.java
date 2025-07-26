package com.databasemeta.ahn.controller.api;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.databasemeta.ahn.dto.TableMetadata;
import com.databasemeta.ahn.service.MetadataService;
import com.databasemeta.ahn.util.ExcelExporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Metadata API", description = "MySQL 메타데이터 조회 및 다운로드")
@RestController
@RequestMapping("/api/metadata")
public class MetadataController {

    private final MetadataService service;

    public MetadataController(MetadataService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, TableMetadata> getMetadata() throws Exception {
        return service.getMetadata();
    }

    @GetMapping(value = "/download/json", produces = "application/json")
    public ResponseEntity<Resource> downloadJson() throws Exception {
        Map<String, TableMetadata> metadata = service.getMetadata();

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        File tempFile = File.createTempFile("metadata-", ".json");
        mapper.writeValue(tempFile, metadata);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(tempFile));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=metadata.json")
                .contentLength(tempFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/download/excel")
    public ResponseEntity<Resource> downloadExcel() throws Exception {
        Map<String, TableMetadata> metadata = service.getMetadata();
        File file = ExcelExporter.exportToExcel(metadata);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=metadata.xlsx")
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


}
