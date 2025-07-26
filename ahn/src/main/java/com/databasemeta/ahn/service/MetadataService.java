package com.databasemeta.ahn.service;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.databasemeta.ahn.dto.TableMetadata;
import com.databasemeta.ahn.extractor.MetadataExtractor;



@Service
public class MetadataService {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String pass;

    @Value("${spring.datasource.database}")
    private String db;

    public Map<String, TableMetadata> getMetadata() throws SQLException {
        MetadataExtractor extractor = new MetadataExtractor(url, db, user, pass);
        return extractor.extract();
    }
}
