package com.databasemeta.ahn.util;

import java.util.Map;


public class Util {
  public static String getValueIgnoreCase(Map<String, Object> map, String key) {
    if (key == null) return "";
    Object value = map.get(key.toLowerCase());
    if (value != null) return String.valueOf(value);
    value = map.get(key.toUpperCase());
    if (value != null) return String.valueOf(value);
    return "";
  }

  public static String extractDbName(String jdbcUrl) {
    // 정규식으로 DB 이름을 추출
    String pattern = "jdbc:mysql://[^/?]+/([^?]+)";
    java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
    java.util.regex.Matcher m = r.matcher(jdbcUrl);
    if (m.find()) {
        String dbWithParams = m.group(1);
        // 파라미터 부분을 제거
        int paramIdx = dbWithParams.indexOf('?');
        if (paramIdx >= 0) {
            return dbWithParams.substring(0, paramIdx);
        }
        return dbWithParams;
    }
    return null;
}

}
