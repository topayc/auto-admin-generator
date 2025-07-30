package com.databasemeta.ahn.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class RawColumnNameMapMapper implements RowMapper<Map<String, Object>> {
    @Override
    public Map<String, Object> map(ResultSet rs, StatementContext ctx) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<>();
        int columnCount = rs.getMetaData().getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            // 컬럼명을 그대로 가져오기 (대소문자 유지)
            String colName = rs.getMetaData().getColumnLabel(i);
            // 또는 getColumnName(i) 사용 가능 - 차이는 alias 처리 여부
            Object val = rs.getObject(i);
            row.put(colName, val);
        }
        return row;
    }
}
