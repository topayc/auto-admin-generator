package com.databasemeta.ahn.extractor;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import com.databasemeta.ahn.dto.TableMetadata;

public class MetadataExtractor {

    private final String url;
    private final String user;
    private final String pass;
    private final String db;

    public MetadataExtractor(String url, String db, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.db = db;
    }

    public Map<String, TableMetadata> extract() throws SQLException {
        Map<String, TableMetadata> map = new LinkedHashMap<>();

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            List<String> tables = MetadataMapper.getTableNames(conn, db);

            for (String table : tables) {
                TableMetadata meta = new TableMetadata();
                meta.columnInfos = MetadataMapper.mapColumnInfo(conn, db, table);
                meta.indexKeys = MetadataMapper.mapIndexInfo(conn, db, table);
                meta.foreignKey = MetadataMapper.mapForeignKeys(conn, db, table);
                meta.primaryKeys = MetadataMapper.mapPrimaryKeys(conn, db, table);
                meta.referenceMes = MetadataMapper.mapReferencedMe(conn, db, table);
                meta.uniqueKeys = MetadataMapper.mapUniqueKeys(conn,db, table);
                map.put(table, meta);
            }
        }

        return map;
    }
}
