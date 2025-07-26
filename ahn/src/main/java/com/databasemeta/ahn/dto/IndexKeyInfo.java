package com.databasemeta.ahn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexKeyInfo {
    public String table;
    public boolean nonUnique;
    public String keyName;
    public int seqInIndex;
    public String columnName;
    public String collation;
    public Long cardinality;
    public String subPart;
    public String packed;
    public String nullable;
    public String indexType;
    public String comment;
    public String indexComment;
    public String visible;
    public String expression;
}
