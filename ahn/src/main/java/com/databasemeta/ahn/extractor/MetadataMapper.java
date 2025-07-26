package com.databasemeta.ahn.extractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.databasemeta.ahn.dto.ColumnInfo;
import com.databasemeta.ahn.dto.ForeignKeyInfo;
import com.databasemeta.ahn.dto.IndexKeyInfo;
import com.databasemeta.ahn.dto.PrimaryKeyInfo;
import com.databasemeta.ahn.dto.ReferenceMeInfo;
import com.databasemeta.ahn.dto.UniqueKeyInfo;



public class MetadataMapper {

    public static List<ColumnInfo> mapColumnInfo(Connection conn, String db, String table) throws SQLException {
        String sql = """
            SELECT * FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?
            ORDER BY ORDINAL_POSITION
        """;

        List<ColumnInfo> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, db);
            ps.setString(2, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ColumnInfo c = new ColumnInfo();
                    c.tableCatalog = rs.getString("TABLE_CATALOG");
                    c.tableSchema = rs.getString("TABLE_SCHEMA");
                    c.tableName = rs.getString("TABLE_NAME");
                    c.columnName = rs.getString("COLUMN_NAME");
                    c.ordinalPosition = rs.getInt("ORDINAL_POSITION");
                    c.columnDefault = rs.getString("COLUMN_DEFAULT");
                    c.isNullable = rs.getString("IS_NULLABLE");
                    c.dataType = rs.getString("DATA_TYPE");
                    c.characterMaximumLength = rs.getObject("CHARACTER_MAXIMUM_LENGTH", Long.class);
                    c.characterOctetLength = rs.getObject("CHARACTER_OCTET_LENGTH", Long.class);
                    c.numericPrecision = rs.getObject("NUMERIC_PRECISION", Integer.class);
                    c.numericScale = rs.getObject("NUMERIC_SCALE", Integer.class);
                    c.datetimePrecision = rs.getObject("DATETIME_PRECISION", Integer.class);
                    c.characterSetName = rs.getString("CHARACTER_SET_NAME");
                    c.collationName = rs.getString("COLLATION_NAME");
                    c.columnType = rs.getString("COLUMN_TYPE");
                    c.columnKey = rs.getString("COLUMN_KEY");
                    c.extra = rs.getString("EXTRA");
                    c.privileges = rs.getString("PRIVILEGES");
                    c.columnComment = rs.getString("COLUMN_COMMENT");
                    c.generationExpression = rs.getString("GENERATION_EXPRESSION");
                    c.srsId = rs.getObject("SRS_ID", Integer.class);
                    list.add(c);
                }
            }
        }

        return list;
    }

    public static List<IndexKeyInfo> mapIndexInfo(Connection conn, String db, String table) throws SQLException {
        String sql = "SHOW INDEX FROM `" + table + "` FROM `" + db + "`";
        List<IndexKeyInfo> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                IndexKeyInfo i = new IndexKeyInfo();
                i.table = rs.getString("Table");
                i.nonUnique = rs.getInt("Non_unique") == 1;
                i.keyName = rs.getString("Key_name");
                i.seqInIndex = rs.getInt("Seq_in_index");
                i.columnName = rs.getString("Column_name");
                i.collation = rs.getString("Collation");
                i.cardinality = rs.getObject("Cardinality", Long.class);
                i.subPart = rs.getString("Sub_part");
                i.packed = rs.getString("Packed");
                i.nullable = rs.getString("Null");
                i.indexType = rs.getString("Index_type");
                i.comment = rs.getString("Comment");
                i.indexComment = rs.getString("Index_comment");
                i.visible = rs.getString("Visible");
                i.expression = rs.getString("Expression");
                list.add(i);
            }
        }
        return list;
    }

    public static List<ForeignKeyInfo> mapForeignKeys(Connection conn, String db, String table) throws SQLException {
        String sql = """
            SELECT 
                kcu.CONSTRAINT_NAME,
                kcu.COLUMN_NAME,
                kcu.REFERENCED_TABLE_NAME,
                kcu.REFERENCED_COLUMN_NAME,
                rc.UPDATE_RULE,
                rc.DELETE_RULE,
                kcu.ORDINAL_POSITION
            FROM information_schema.KEY_COLUMN_USAGE kcu
            JOIN information_schema.REFERENTIAL_CONSTRAINTS rc 
                ON kcu.CONSTRAINT_NAME = rc.CONSTRAINT_NAME
               AND kcu.CONSTRAINT_SCHEMA = rc.CONSTRAINT_SCHEMA
            WHERE kcu.TABLE_SCHEMA = ? AND kcu.TABLE_NAME = ? AND kcu.REFERENCED_TABLE_NAME IS NOT NULL
        """;

        List<ForeignKeyInfo> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, db);
            ps.setString(2, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ForeignKeyInfo f = new ForeignKeyInfo();
                    f.constraintName = rs.getString("CONSTRAINT_NAME");
                    f.columnName = rs.getString("COLUMN_NAME");
                    f.referencedTableName = rs.getString("REFERENCED_TABLE_NAME");
                    f.referencedColumnName = rs.getString("REFERENCED_COLUMN_NAME");
                    f.updateRule = rs.getString("UPDATE_RULE");
                    f.deleteRule = rs.getString("DELETE_RULE");
                    f.ordinalPosition = rs.getInt("ORDINAL_POSITION");
                    list.add(f);
                }
            }
        }

        return list;
    }

    public static List<PrimaryKeyInfo> mapPrimaryKeys(Connection conn, String db, String table) throws SQLException {
        String sql = """
            SELECT COLUMN_NAME, CONSTRAINT_NAME, ORDINAL_POSITION 
            FROM information_schema.KEY_COLUMN_USAGE 
            WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND CONSTRAINT_NAME = 'PRIMARY'
        """;

        List<PrimaryKeyInfo> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, db);
            ps.setString(2, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PrimaryKeyInfo p = new PrimaryKeyInfo();
                    p.columnName = rs.getString("COLUMN_NAME");
                    p.constraintName = rs.getString("CONSTRAINT_NAME");
                    p.ordinalPosition = rs.getInt("ORDINAL_POSITION");
                    list.add(p);
                }
            }
        }

        return list;
    }

    public static List<ReferenceMeInfo> mapReferencedMe(Connection conn, String db, String table) throws SQLException {
        String sql = """
            SELECT 
              kcu.REFERENCED_TABLE_NAME AS table_name,
              kcu.REFERENCED_COLUMN_NAME AS column_name,
              kcu.TABLE_NAME AS referencedMeTable,
              kcu.COLUMN_NAME AS referencedMeColumn
            FROM information_schema.KEY_COLUMN_USAGE kcu
            WHERE kcu.TABLE_SCHEMA = ? AND kcu.REFERENCED_TABLE_NAME = ?
        """;

        List<ReferenceMeInfo> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, db);
            ps.setString(2, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReferenceMeInfo r = new ReferenceMeInfo();
                    r.tableName = rs.getString("table_name");
                    r.columnName = rs.getString("column_name");
                    r.referencedMeTable = rs.getString("referencedMeTable");
                    r.referencedMeColumn = rs.getString("referencedMeColumn");
                    list.add(r);
                }
            }
        }

        return list;
    }

    public static List<String> getTableNames(Connection conn, String db) throws SQLException {
    String sql = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = ?";
    List<String> list = new ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, db);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("TABLE_NAME"));
            }
        }
    }
    return list;
}
public static List<UniqueKeyInfo> mapUniqueKeys(Connection conn, String db, String table) throws SQLException {
    //System.out.println("db : " + db);
    //System.out.println("table : " +table);
    String sql = """
        SELECT 
            kcu.TABLE_SCHEMA,
            kcu.TABLE_NAME,
            kcu.CONSTRAINT_NAME,
            kcu.COLUMN_NAME,
            kcu.ORDINAL_POSITION,
            kcu.CONSTRAINT_NAME AS INDEX_NAME
        FROM information_schema.TABLE_CONSTRAINTS tc
        JOIN information_schema.KEY_COLUMN_USAGE kcu 
        ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME 
        AND tc.TABLE_SCHEMA = kcu.TABLE_SCHEMA
        AND tc.TABLE_NAME = kcu.TABLE_NAME
        WHERE 
            tc.TABLE_SCHEMA = ? 
            AND tc.TABLE_NAME = ?
            AND tc.CONSTRAINT_TYPE = 'UNIQUE' 
        ORDER BY 
            kcu.CONSTRAINT_NAME, 
            kcu.ORDINAL_POSITION;
    """;

    List<UniqueKeyInfo> list = new ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, db);
        ps.setString(2, table);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UniqueKeyInfo u = new UniqueKeyInfo();
                u.tableSchema = rs.getString("TABLE_SCHEMA");
                u.tableName = rs.getString("TABLE_NAME");
                u.constraintName = rs.getString("CONSTRAINT_NAME");
                u.columnName = rs.getString("COLUMN_NAME");
                u.ordinalPosition = rs.getInt("ORDINAL_POSITION");

                /*만약 일부 MySQL 버전에서 INDEX_NAME 컬럼이 없다면, CONSTRAINT_NAME을 대신 넣음 */
                u.indexName = /* rs.getString("INDEX_NAME") != null ? 
                    rs.getString("INDEX_NAME") :*/ rs.getString("INDEX_NAME") ;
                list.add(u);
            }
        }
    }
    return list;
}


}
