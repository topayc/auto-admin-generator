package com.databasemeta.ahn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReferenceMeInfo {
    public String tableName;
    public String columnName;
    public String referencedMeTable;
    public String referencedMeColumn;
}
