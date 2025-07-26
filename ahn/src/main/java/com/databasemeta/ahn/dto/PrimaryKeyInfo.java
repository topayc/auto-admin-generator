package com.databasemeta.ahn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrimaryKeyInfo {
    public String constraintName;
    public String columnName;
    public int ordinalPosition;
}
