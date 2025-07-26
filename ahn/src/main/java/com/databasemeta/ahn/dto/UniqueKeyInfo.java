package com.databasemeta.ahn.dto;

public class UniqueKeyInfo {
    public String tableSchema;
    public String tableName;
    public String constraintName;  // 유니크 키 제약조건 이름
    public String columnName;
    public int ordinalPosition;
    public String indexName;       // 인덱스 이름 (대체로 constraintName과 같음)
    
    @Override
    public String toString() {
        return "UniqueKeyInfo{" +
               "tableSchema='" + tableSchema + '\'' +
               ", tableName='" + tableName + '\'' +
               ", constraintName='" + constraintName + '\'' +
               ", columnName='" + columnName + '\'' +
               ", ordinalPosition=" + ordinalPosition +
               ", indexName='" + indexName + '\'' +
               '}';
    }
}
