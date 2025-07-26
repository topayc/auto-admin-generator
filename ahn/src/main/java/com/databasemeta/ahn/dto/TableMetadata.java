package com.databasemeta.ahn.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableMetadata {
    public List<ColumnInfo> columnInfos;
    public List<IndexKeyInfo> indexKeys;
    public List<PrimaryKeyInfo> primaryKeys;
    public List<ForeignKeyInfo> foreignKey;
    public List<ReferenceMeInfo> referenceMes;
    public List<UniqueKeyInfo> uniqueKeys;
}
