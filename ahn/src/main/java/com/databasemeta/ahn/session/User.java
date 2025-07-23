package com.databasemeta.ahn.session;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** 클래스의 모든 final 변수에 대한 생성자를 생성함 */
@AllArgsConstructor

/** 모든 멤버 변수에 대한 setter 생성 */
@Getter

/** 모든 멤버 변수에 대한 getter 생성 */
@Setter

/** 객체를 생성시 빌더 방식으로 생성할 수 있는 메서드를 자동 추가 */
@Builder
public class User {
  private String projectName;
  private String url;
  private String name;
  private String password;
  private String dbms;
  private String driverClassName;
  private String css;
  private String uuid;
  private Map<String, ArrayList<Map<String, String>>> metadataTableMap;
  private Map<String, ArrayList<Map<String, String>>> metadataFeildSummaryMap;

  /**
   * jdbc:mysql://localhost:3306/demo 식의 연결 정보에서 DB 이름만 추출(여기서는 demo) 
   * @return 추출된 DB 문자열
   */
  public String extractDbName() {
    // jdbc:mysql://localhost:3306/demo?useSSL=false...
    String regex = "^jdbc:mysql://[^/]+:\\d+/(\\w+)";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(this.url);
    if (matcher.find()) {
        return matcher.group(1);
    }
    return null;
  }
  

}
