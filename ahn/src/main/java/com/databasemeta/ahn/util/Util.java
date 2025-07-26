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

}
