package com.databasemeta.ahn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForeignKeyInfo {
    public String constraintName;
    public String columnName;
    public String referencedTableName;
    public String referencedColumnName;
    public String updateRule;
    public String deleteRule;
    public int ordinalPosition;
}
