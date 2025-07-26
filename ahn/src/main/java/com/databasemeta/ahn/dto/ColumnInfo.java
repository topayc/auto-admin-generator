package com.databasemeta.ahn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnInfo {
    public String tableCatalog;
    public String tableSchema;
    public String tableName;
    public String columnName;
    public int ordinalPosition;
    public String columnDefault;
    public String isNullable;
    public String dataType;
    public Long characterMaximumLength;
    public Long characterOctetLength;
    public Integer numericPrecision;
    public Integer numericScale;
    public Integer datetimePrecision;
    public String characterSetName;
    public String collationName;
    public String columnType;
    public String columnKey;
    public String extra;
    public String privileges;
    public String columnComment;
    public String generationExpression;
    public Integer srsId;
}
