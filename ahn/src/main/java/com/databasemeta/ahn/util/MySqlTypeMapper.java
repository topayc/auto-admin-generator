package com.databasemeta.ahn.util;

import java.util.HashMap;
import java.util.Map;

public class MySqlTypeMapper {

    // MySQL 타입 상수 정의
    public static final String VARCHAR = "VARCHAR";
    public static final String CHAR = "CHAR";
    public static final String TEXT = "TEXT";
    public static final String INT = "INT";
    public static final String INTEGER = "INTEGER";
    public static final String BIGINT = "BIGINT";
    public static final String SMALLINT = "SMALLINT";
    public static final String TINYINT = "TINYINT";
    public static final String MEDIUMINT = "MEDIUMINT";
    public static final String FLOAT = "FLOAT";
    public static final String DOUBLE = "DOUBLE";
    public static final String DECIMAL = "DECIMAL";
    public static final String DATE = "DATE";
    public static final String DATETIME = "DATETIME";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String TIME = "TIME";
    public static final String YEAR = "YEAR";
    public static final String BLOB = "BLOB";
    public static final String BIT = "BIT";
    public static final String BOOLEAN = "BOOLEAN";
    public static final String JSON = "JSON";
    public static final String ENUM = "ENUM";
    public static final String SET = "SET";

    private static final Map<String, String> MYSQL_TO_JAVA_TYPE = new HashMap<>();

    static {
        // 문자열 계열
        MYSQL_TO_JAVA_TYPE.put(VARCHAR, "String");
        MYSQL_TO_JAVA_TYPE.put(CHAR, "String");
        MYSQL_TO_JAVA_TYPE.put(TEXT, "String");
        MYSQL_TO_JAVA_TYPE.put(ENUM, "String");
        MYSQL_TO_JAVA_TYPE.put(SET, "String");

        // 숫자 계열
        MYSQL_TO_JAVA_TYPE.put(INT, "Integer");
        MYSQL_TO_JAVA_TYPE.put(INTEGER, "Integer");
        MYSQL_TO_JAVA_TYPE.put(SMALLINT, "Short");
        MYSQL_TO_JAVA_TYPE.put(TINYINT, "Byte");
        MYSQL_TO_JAVA_TYPE.put(MEDIUMINT, "Integer");
        MYSQL_TO_JAVA_TYPE.put(BIGINT, "Long");
        MYSQL_TO_JAVA_TYPE.put(FLOAT, "Float");
        MYSQL_TO_JAVA_TYPE.put(DOUBLE, "Double");
        MYSQL_TO_JAVA_TYPE.put(DECIMAL, "java.math.BigDecimal");

        // 날짜/시간 계열
        MYSQL_TO_JAVA_TYPE.put(DATE, "java.time.LocalDate");
        MYSQL_TO_JAVA_TYPE.put(DATETIME, "java.time.LocalDateTime");
        MYSQL_TO_JAVA_TYPE.put(TIMESTAMP, "java.sql.Timestamp");
        MYSQL_TO_JAVA_TYPE.put(TIME, "java.time.LocalTime");
        MYSQL_TO_JAVA_TYPE.put(YEAR, "Integer");

        // 기타
        MYSQL_TO_JAVA_TYPE.put(BLOB, "byte[]");
        MYSQL_TO_JAVA_TYPE.put(BIT, "Boolean");
        MYSQL_TO_JAVA_TYPE.put(BOOLEAN, "Boolean");
        MYSQL_TO_JAVA_TYPE.put(JSON, "String");
    }

    /**
     * MySQL 타입 문자열을 받아서 매칭되는 Java 타입 문자열을 반환
     * (대소문자 구분 없이 동작)
     */
    public static String toJavaType(String mysqlType) {
        if (mysqlType == null) return "Object";
        String type = mysqlType.toUpperCase();
        // 타입에 길이 정보가 붙은 경우(ex: VARCHAR(255)), 괄호 앞까지만 추출
        int idx = type.indexOf('(');
        if (idx > 0) {
            type = type.substring(0, idx);
        }
        return MYSQL_TO_JAVA_TYPE.getOrDefault(type, "Object");
    }

        /**
     * 전체 타입명(java.time.LocalDateTime 등)에서 마지막 타입 이름만 추출
     * 예: "java.time.LocalDateTime" -> "LocalDateTime"
     *     "String" -> "String"
     */

  public static String toShortTypeName(String typeName) {
    if (typeName == null) return "Object";
    int idx = typeName.lastIndexOf('.');
    if (idx >= 0 && idx < typeName.length() - 1) {
        return typeName.substring(idx + 1);
    }
    return typeName;
}
}
