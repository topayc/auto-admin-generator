package com.databasemeta.ahn.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.databasemeta.ahn.session.SessionInfo;
import com.databasemeta.ahn.util.MySqlTypeMapper;
import com.databasemeta.ahn.util.MysqlTypeToHtmlTag;
import com.databasemeta.ahn.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.UnaryCondition;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class MainService {
 

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String userName;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${spring.datasource.driver-class-name}")
  private String driverClassName;

  private  SessionManager sessionManager;

  /**
   * 데이다 베이스에서 표준으로 제공하는 표준 메타데이타 정보가져와서 변환한 후 반환
   * @param dataSource
   * @return 메타 데이타를 Map<String, ArrayList<Map<String, String>>> 형식으로 변환한 자료 구조
   * @throws Exception
   */
  @Transactional
  public Map<String, ArrayList<Map<String, String>>> getDatabaseMetaData(DataSource dataSource) throws Exception {
    try (Connection conn = dataSource.getConnection()) {
      DatabaseMetaData metaData = conn.getMetaData();

      StringBuilder tableDataBuilder = new StringBuilder();
      Map<String, ArrayList<Map<String, String>>> tableMap = new HashMap<>();

      // 모든 테이블 목록 가져오기
      try (ResultSet tablesSet = metaData.getTables("demo", null, "%", new String[] { "TABLE" })) {
        while (tablesSet.next()) {
          String tableName = tablesSet.getString("TABLE_NAME");
          tableDataBuilder.append(tableName + " 테이블</br>");

          ArrayList<Map<String, String>> columnList = new ArrayList<>();
          try (ResultSet columnsSet = metaData.getColumns("demo", null, tableName, "%")) {
            while (columnsSet.next()) {
              String columnName = columnsSet.getString("COLUMN_NAME");
              String columnType = columnsSet.getString("TYPE_NAME");
              int columnSize = columnsSet.getInt("COLUMN_SIZE");
              String isNullable = columnsSet.getString("IS_NULLABLE"); // "YES" or "NO"
              String columnDef = columnsSet.getString("COLUMN_DEF"); // 기본값
              String columnComment = columnsSet.getString("REMARKS"); // ★ 주석 추출

              // if (columnComment == null || "".equals(columnComment) || "
              // ".equals(columnComment)){
              String sql = "SELECT COLUMN_COMMENT FROM information_schema.COLUMNS " +
                  "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?";
              try (PreparedStatement pt = conn.prepareStatement(sql)) {
                pt.setString(1, "demo"); // DB명
                pt.setString(2, tableName); // 테이블명
                pt.setString(3, columnName); // 컬럼명
                try (ResultSet rs = pt.executeQuery()) {
                  if (rs.next()) {
                    columnComment = rs.getString("COLUMN_COMMENT");
                    System.err.println("주석 찾음 : " + columnComment);
                  }
                }
              }
              // }

              // tableDataBuilder.append(String.format(" 컬럼: %s, 타입: %s, 크기: %d , 주석 :
              // %s</br>", columnName, columnType, columnSize, columnComment));

              Map<String, String> columnMap = new HashMap<>();
              columnMap.put("COLUMN_NAME", columnName);
              columnMap.put("TYPE_NAME", MySqlTypeMapper.toJavaType(columnType));
              columnMap.put("TYPE_NAME_SHORT", MySqlTypeMapper.toShortTypeName(columnType));
              columnMap.put("COLUMN_SIZE", String.valueOf(columnSize));
              columnMap.put("REMARKS", columnComment);
              // columnMap.put("HTML_TAG", MysqlTypeToHtmlTag.getHtmlInputTag(columnName,
              // columnType));
              columnMap.put("CHARSET", "UTF-7");
              columnMap.put("COLLATION", "COLLATION");
              columnMap.put("NULLABLE", isNullable);
              columnMap.put("DEFAULT_VALUE", columnDef);

              if ("ENUM".equalsIgnoreCase(columnType)) {
                // 실제 ENUM 값 목록을 얻기 위해 information_schema.COLUMNS 조회
                String sql2 = "SELECT COLUMN_TYPE FROM information_schema.COLUMNS " +
                    "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?";
                try (PreparedStatement pt = conn.prepareStatement(sql2)) {
                  pt.setString(1, "demo"); // DB명
                  pt.setString(2, tableName); // 테이블명
                  pt.setString(3, columnName); // 컬럼명
                  try (ResultSet rs = pt.executeQuery()) {
                    if (rs.next()) {
                      String columnTypeFull = rs.getString("COLUMN_TYPE"); // 예: ENUM('A','B','C')
                      // System.out.printf(" 컬럼: %s, 타입: %s, 크기: %d ,풀타입 : %s\n", columnName,
                      // columnType, columnSize, columnTypeFull);
                      // columnMap.put("HTML_TAG", MysqlTypeToHtmlTag.getHtmlInputTag(columnName,
                      // columnTypeFull));
                      // 파싱해서 옵션 추출
                      // int start = columnTypeFull.indexOf('(');
                      // int end = columnTypeFull.lastIndexOf(')');
                      // if (start > 0 && end > start) {
                      // String optionsStr = columnTypeFull.substring(start + 1, end);
                      // String[] options = optionsStr.split(",");
                      // for (String opt : options) {
                      // String value = opt.trim().replaceAll("^'(.*)'$", "JsonProcessingException");
                      // System.out.println("ENUM 값: " + value);
                      // // 여기서 <option> 태그 생성 등 원하는 처리를 하면 됩니다
                      // }
                      // }
                    }
                  }
                }

              } else {
                // ENUM이 아닐 때 기존 처리
              }

              // System.out.printf(" 컬럼: %s, 타입: %s, 크기: %s , 풀타입 : %s 주석 : %s\n",
              // columnMap.get("COLUMN_NAME"),
              // columnMap.get("TYPE_NAME"),
              // columnMap.get("TYPE_NAME_SHORT"),
              // columnMap.get("COLUMN_SIZE"),
              // columnMap.get("REMARKS"),
              // columnMap.get("HTML_TAG"));

              columnList.add(columnMap);
            }
          }
          tableMap.put(tableName, columnList);
        }
      }
      return tableMap;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
    }
    return null;
  }

  /**
   * Mysql 에 종속적인 관련한 메타 데이타를 자여와서 뱁 구조로 변경하여 반환
   * @param dataSource
   * @return Map<String, ArrayList<Map<String, String>>> 형태도 변경된 메타데이타
   * @throws Exception
   */
  @Transactional
  public Map<String, ArrayList<Map<String, String>>> getMysqlDatabaseMetaData(DataSource dataSource) throws Exception {
    try (Connection conn = dataSource.getConnection()) {
      DatabaseMetaData metaData = conn.getMetaData();

      StringBuilder tableDataBuilder = new StringBuilder();
      Map<String, ArrayList<Map<String, String>>> tableMap = new HashMap<>();

      // 모든 테이블 목록 가져오기
      try (ResultSet tablesSet = metaData.getTables("demo", null, "%", new String[] { "TABLE" })) {
        while (tablesSet.next()) {
          String tableName = tablesSet.getString("TABLE_NAME");
          tableDataBuilder.append(tableName + " 테이블</br>");

          ArrayList<Map<String, String>> columnList = new ArrayList<>();

          String sql = "SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION";
          try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "demo");
            ps.setString(2, tableName);
            ResultSet rs = ps.executeQuery();

            // ResultSetMetaData meta = rs.getMetaData();
            // int columnCount = meta.getColumnCount();

            while (rs.next()) {
              Map<String, String> columnMap = new LinkedHashMap<>();
              String columnName = rs.getString("COLUMN_NAME");
              String columnType = rs.getString("COLUMN_TYPE");
              String dataType = rs.getString("DATA_TYPE");
              String columnKey = rs.getString("COLUMN_KEY");
              String columnComments = rs.getString("COLUMN_COMMENT");
              String extra = rs.getString("EXTRA");

              columnMap.put("COLUMN_NAME", columnName);
              columnMap.put("COLUMN_NAME_UPPER", columnName.toUpperCase());
              columnMap.put("COLUMN_TYPE", columnType);
              columnMap.put("DATA_TYPE", dataType);

              columnMap.put("COLUMN_DEFAULT", rs.getString("COLUMN_DEFAULT"));
              columnMap.put("IS_NULLABLE", rs.getString("IS_NULLABLE"));
              columnMap.put("CHARACTER_MAXIMUM_LENGTH", rs.getString("CHARACTER_MAXIMUM_LENGTH"));
              columnMap.put("CHARACTER_OCTET_LENGTH", rs.getString("CHARACTER_OCTET_LENGTH"));
              columnMap.put("NUMERIC_PRECISION", rs.getString("NUMERIC_PRECISION"));
              columnMap.put("NUMERIC_SCALE", rs.getString("NUMERIC_SCALE"));
              columnMap.put("DATETIME_PRECISION", rs.getString("DATETIME_PRECISION"));
              columnMap.put("CHARACTER_SET_NAME", rs.getString("CHARACTER_SET_NAME"));
              columnMap.put("COLLATION_NAME", rs.getString("COLLATION_NAME"));
              columnMap.put("PRIVILEGES", rs.getString("PRIVILEGES"));
              columnMap.put("COLUMN_COMMENT", columnComments);
              columnMap.put("GENERATION_EXPRESSION", rs.getString("GENERATION_EXPRESSION"));
              columnMap.put("COLUMN_KEY", columnKey);
              columnMap.put("EXTRA", extra);
              columnMap.put("PROGRAMMING_LANG", "JAVA TYPE");
              columnMap.put("PROGRAMMING_LANG_TYPE", MySqlTypeMapper.toJavaType(columnType));

              //인텍스, 왜래기, 기본키, 유니크 제약조건을 위한 키를 미리 할당
              columnMap.put("CONSTRAINT_NAME", "");
              columnMap.put("CONSTRAINT_TYPE", "");
              columnMap.put("REFERENCED_TABLE_NAME", "");
              columnMap.put("REFERENCED_COLUMN_NAME", "");

              columnMap.put("INDEX_NAME", "");
              columnMap.put("NON_UNIQUE", "");
              columnMap.put("INDEX_TYPE", "");
              //-> 기본 빈 할당 작업 완료

              columnMap.put("STYLED_HTML_TAG", MysqlTypeToHtmlTag.generateStyledSearchInputTag(tableName, columnName,
                  columnType, columnKey, columnComments));
              columnMap.put("COL_WRAPPED_STYLED_HTML_TAG", MysqlTypeToHtmlTag.generateStyledColWrapedSearchInputTag(tableName, columnName,
                  columnType, columnKey, columnComments, "col-md-4 col-sm-6 col-12"));
              columnMap.put("COL_WRAPPED_STYLED_CREATE_TAG", MysqlTypeToHtmlTag.generateStyledCrateInputTag(tableName, columnName,extra,
                  columnType, columnKey, columnComments, "col-md-4 col-sm-6 col-12"));
              columnMap.put("HTML_TAG",
                  MysqlTypeToHtmlTag.generateRawSearchInputTag(tableName, columnName, columnType, columnKey, columnComments));
              // if (dataType.equalsIgnoreCase("enum")) {
              // columnMap.put("HTML_TAG", MysqlTypeToHtmlTag.getHtmlInputTag(columnName,
              // dataType,columnKey,columnComments ));
              // }
              columnList.add(columnMap);
            }
          }
          tableMap.put(tableName, columnList);

        }
      }
      Map<String, ArrayList<Map<String, String>>> sortedMap = new TreeMap<>(tableMap);
      
  
      return sortedMap;

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
    }
    return null;
  }

  /**
  * 제약 조건에 대한 세부 정보는 별도의 테이블에 있기 때문에 그 정보를 가져와서 기존 메타데이타 자로 구조 맵에 병합
  * @param sessionInfo 현제 세션의 내용, 1차저인 메타데이타는 세션에 저장되어 있기 때문에 초기 접속시 필요함 
  * @param tableMap
  * @throws Exception
  */
  public  void mergeConstraints(SessionInfo sessionInfo,  Map<String, ArrayList<Map<String, String>>> tableMap) throws Exception {
   
    Jdbi jdbi = Jdbi.create(sessionInfo.getDatasource());
    //System.out.println("====================머지를 시작합니다");
    for (Map.Entry<String, ArrayList<Map<String, String>>> tableEntry : tableMap.entrySet()){
     
      String userTableName = tableEntry.getKey();
      // DbSpec spec = new DbSpec();
      // DbSchema schema = new DbSchema(spec, "information_schema");
      // DbTable table = new DbTable(schema, "KEY_COLUMN_USAGE");
  
      // DbColumn colTableName = new DbColumn(table, "TABLE_NAME", null);
      // DbColumn colColName = new DbColumn(table, "COLUMN_NAME", null);
      // DbColumn colConstraint = new DbColumn(table, "CONSTRAINT_NAME", null);
      // DbColumn colRefTableName = new DbColumn(table, "REFERENCED_TABLE_NAME", null);
      // DbColumn colRefColname = new DbColumn(table, "REFERENCED_COLUMN_NAME", null);
      // DbColumn colTableScheme = new DbColumn(table, "TABLE_SCHEMA", null);
      // DbColumn colPriKey = new DbColumn(table, "PRIMARY", null);

      // SelectQuery query = new SelectQuery()
      // .addAllTableColumns(table)
      // .addCustomFromTable(table)
      // .addCondition(BinaryCondition.equalTo(colTableScheme, sessionInfo.getUser().extractDbName()))
      // .addCondition(BinaryCondition.equalTo(colTableName, userTableName))
      // .addCondition(
      //   ComboCondition.or(
      //       BinaryCondition.equalTo(colConstraint, "PRIMARY"),
      //       Conditions.isNotNull(colRefTableName)
      //   )
      // );
    
      // List<Map<String, Object>> mapList = jdbi.withHandle(handle -> handle.createQuery(query.validate().toString()).mapToMap().list());
     
      /* 기본기 왜래키 제약 조건을 기존 테이블 맵에 병합  */ 
      // for (Map<String, Object> resultMap : mapList){
      //   if (resultMap.get("CONSTRAINT_NAME") != null){
      //     for (Map<String, String> tableColumnMap : tableEntry.getValue()){
      //       if (tableColumnMap.get("COLUMN_NAME").equals(resultMap.get("COLUMN_NAME").toString())){
      //         tableColumnMap.put("CONSTRAINT_NAME", resultMap.get("CONSTRAINT_NAME").toString());
      //         tableColumnMap.put("CONSTRAINT_TYPE", resultMap.get("CONSTRAINT_NAME").toString().equalsIgnoreCase("PRIMARY KEY") ? "PRIMARY KEY" : "FOREIGN KEY");
      //         tableColumnMap.put("REFERENCED_TABLE_NAME", resultMap.get("REFERENCED_TABLE_NAME") == null ? "" : resultMap.get("REFERENCED_TABLE_NAME").toString() );
      //         tableColumnMap.put("REFERENCED_COLUMN_NAME", resultMap.get("REFERENCED_COLUMN_NAME") == null ? "" : resultMap.get("REFERENCED_COLUMN_NAME").toString() );
      //         tableColumnMap.put("TABLE_CATALOGUE", resultMap.get("TABLE_CATALOGUE") == null ? "" : resultMap.get("TABLE_CATALOGUE").toString() );
      //         break;
      //       }
      //     }
      //   }
      // }

    // --> 여기 까지 왜래키 및 기본키 정보를 기존 테이블 맵에 머지 
    /* 아래의 방식은 유니크, 기본키, 왜래키 정보를 한번에 가져옴 . 이걸 사용하면 위의 줄은 지음  */
    String sql = """
        SELECT
          tc.CONSTRAINT_TYPE,
          kcu.CONSTRAINT_NAME,
          kcu.COLUMN_NAME,
          kcu.REFERENCED_TABLE_NAME,
          kcu.REFERENCED_COLUMN_NAME
        FROM
          INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
        JOIN
          INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
          ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
          AND tc.TABLE_SCHEMA = kcu.TABLE_SCHEMA
          AND tc.TABLE_NAME = kcu.TABLE_NAME
        WHERE
          tc.TABLE_SCHEMA = :dbName
          AND tc.TABLE_NAME = :tableName
          AND tc.CONSTRAINT_TYPE IN ('PRIMARY KEY', 'FOREIGN KEY', 'UNIQUE')
        ORDER BY
          tc.CONSTRAINT_TYPE, kcu.CONSTRAINT_NAME, kcu.COLUMN_NAME;

        """;
    
        List<Map<String, Object>> constaintsList  = jdbi.withHandle(handle -> handle.createQuery(sql)
        .bind("dbName", sessionInfo.getUser().extractDbName())
        .bind("tableName", userTableName)
        .mapToMap()
        .list()); 

        //System.out.println("-----------------------------  왜래키 유니크 기본키 구하기");
        //ObjectMapper mapper = new ObjectMapper();
        //mapper.enable(SerializationFeature.INDENT_OUTPUT); 
        //String json = mapper.writeValueAsString(tableMap);
        //System.out.println(json);

        for (Map<String, Object> resultMap : constaintsList){
          String columnName  = Util.getValueIgnoreCase(resultMap, "column_name");
          String constraintType = Util.getValueIgnoreCase(resultMap, "constraint_type");
          String constraintName= Util.getValueIgnoreCase(resultMap, "constraint_name");
          String referencedTableName = Util.getValueIgnoreCase(resultMap, "referenced_table_name");
          String referencedColumnName = Util.getValueIgnoreCase(resultMap, "referenced_column_name");

    

          for (Map<String, String> tableColumnMap : tableEntry.getValue()){
            if (tableColumnMap.get("COLUMN_NAME").equalsIgnoreCase(columnName == null ? "" : columnName)){
              tableColumnMap.put("CONSTRAINT_NAME", constraintName == null ? "" : constraintName);
              tableColumnMap.put("CONSTRAINT_TYPE", constraintType == null ? "" : constraintType);
              tableColumnMap.put("REFERENCED_TABLE_NAME", referencedTableName == null ? "" : referencedTableName );
              tableColumnMap.put("REFERENCED_COLUMN_NAME", referencedColumnName == null ? "" : referencedColumnName);
              break;
            }
          }
        }

        //-> 유니크키 , 기본키, 왜래키 병합 완료 

        /*인덱스 조회 및 병합 */
        String indexQuery = """
          SELECT
            INDEX_NAME,
            NON_UNIQUE,
            COLUMN_NAME,
            INDEX_TYPE
          FROM
            INFORMATION_SCHEMA.STATISTICS
          WHERE
            TABLE_SCHEMA = :dbName
            AND TABLE_NAME = :tableName
          ORDER BY
            INDEX_NAME, SEQ_IN_INDEX;
            """;
        List<Map<String, Object>> indexList  = jdbi.withHandle(handle -> handle.createQuery(sql)
            .bind("dbName", sessionInfo.getUser().extractDbName())
            .bind("tableName", userTableName)
            .mapToMap()
            .list()); 
        
            //System.out.println("-----------------------------  인덱스구하기");
            //ObjectMapper mapper2 = new ObjectMapper();
            //mapper2.enable(SerializationFeature.INDENT_OUTPUT); 
            //String json2 = mapper.writeValueAsString(tableMap);
            //System.out.println(json2);

        for (Map<String, Object> resultMap : indexList){
          String indexName = Util.getValueIgnoreCase(resultMap, "index_name");
          String nonUnique= Util.getValueIgnoreCase(resultMap, "non_unique");
          String columnName = Util.getValueIgnoreCase(resultMap, "column_name");
          String indexType  = Util.getValueIgnoreCase(resultMap, "index_type");
    
          for (Map<String, String> tableColumnMap : tableEntry.getValue()){
            if (tableColumnMap.get("COLUMN_NAME").equalsIgnoreCase(columnName == null ? "" : columnName)){
                tableColumnMap.put("INDEX_NAME", indexName == null ? "" : indexName);
                tableColumnMap.put("NON_UNIQUE", nonUnique == null ? "" : nonUnique);
                tableColumnMap.put("INDEX_TYPE", indexType == null ? "" : indexType);
                break;
              }
            }
        }   
    }
   
    //유니크, 왜래키, 기본키, 인덱스 병합 완료
  }

  /**
   * 각 테이블의 데이타를  셀렉트 해서 HTML 태그로 만들고 이를 맵리스트로 반환
   * @param request
   * @param tableName
   * @return
   * @throws JsonProcessingException
   */
  @Transactional
  public List<Map<String, Object>> getDataList(HttpServletRequest request, String tableName)
      throws JsonProcessingException {
    HikariDataSource dataSource = ((SessionInfo) request.getSession().getAttribute("sessionInfo")).getDatasource();

    String query = "select * from " + tableName;
    Jdbi jdbi = Jdbi.create(dataSource);
    List<Map<String, Object>> mapList = jdbi.withHandle(handle -> handle.createQuery(query)
        .mapToMap()
        .list());

    return mapList;
  }

  /**
   * 각 테이블의 제약조건을 맵리스트로 반환, 처음 접속시에 모든 메타데이타를 가져오기 때문에 사실상 필료가 없지만 , 이를 호출하는 코드를 정리하지 못해서 일단 남겨둠
   * 삭제 예정
   * @param sessionInfo
   * @param tableName
   * @param constraint
   * @return
   */
  public List<Map<String, Object>> getConstraintData(SessionInfo sessionInfo, String tableName, String constraint) {

    DbSpec spec = new DbSpec();
    DbSchema schema = new DbSchema(spec, "information_schema");
    DbTable table = new DbTable(schema, "KEY_COLUMN_USAGE");

    DbColumn colTableName = new DbColumn(table, "TABLE_NAME", null);
    DbColumn colColName = new DbColumn(table, " COLUMN_NAME", null);
    DbColumn colConstraint = new DbColumn(table, "CONSTRAINT_NAME", null);
    DbColumn colRefTableName = new DbColumn(table, "REFERENCED_TABLE_NAME", null);
    DbColumn colRefColname = new DbColumn(table, "REFERENCED_COLUMN_NAME", null);
    DbColumn colTableScheme = new DbColumn(table, "TABLE_SCHEMA", null);
    DbColumn colPriKey = new DbColumn(table, "PRIMARY", null);

    // SELECT 쿼리 생성
    SelectQuery query = new SelectQuery()
        .addColumns(colTableName, colColName, colConstraint, colRefTableName, colRefColname)
        .addCustomFromTable(table)
        .addCondition(BinaryCondition.equalTo(colTableScheme, sessionInfo.getUser().extractDbName()))
        .addCondition(BinaryCondition.equalTo(colTableName, tableName));

    Jdbi jdbi = Jdbi.create(sessionInfo.getDatasource());
    if (constraint.equalsIgnoreCase("primary")) {
      query.addCondition(BinaryCondition.equalTo(colConstraint, constraint));
      List<Map<String, Object>> mapList = jdbi.withHandle(handle -> handle.createQuery(query.validate().toString())
          .mapToMap()
          .list());
      return mapList;

    } else if (constraint.equalsIgnoreCase("foreign")) {
      query.addCondition(UnaryCondition.isNotNull(colRefTableName));
      List<Map<String, Object>> mapList = jdbi.withHandle(handle -> handle.createQuery(query.validate().toString())
          .mapToMap()
          .list());
      return mapList;

    } else if (constraint.equalsIgnoreCase("unique")) {
      String queryString = """
          SELECT
            kcu.TABLE_NAME,
            kcu.CONSTRAINT_NAME,
            kcu.COLUMN_NAME
          FROM
            information_schema.TABLE_CONSTRAINTS tc
            JOIN information_schema.KEY_COLUMN_USAGE kcu
              ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
             AND tc.TABLE_SCHEMA = kcu.TABLE_SCHEMA
             AND tc.TABLE_NAME = kcu.TABLE_NAME
          WHERE
            tc.CONSTRAINT_TYPE = 'UNIQUE'
            AND tc.TABLE_SCHEMA = :dbName
            AND tc.TABLE_NAME = :tableName
          """;

      List<Map<String, Object>> uniqueInfoList = jdbi.withHandle(handle -> handle.createQuery(queryString)
          .bind("dbName", sessionInfo.getUser().extractDbName())
          .bind("tableName", tableName)
          .mapToMap()
          .list());

      return uniqueInfoList;

    } else if (constraint.equalsIgnoreCase("index")) {
      String queryString = """
          SELECT
              TABLE_NAME,
              INDEX_NAME,
              COLUMN_NAME,
              NON_UNIQUE,
              SEQ_IN_INDEX,
              INDEX_TYPE,
              COLLATION,
              CARDINALITY,
              SUB_PART,
              PACKED,
              NULLABLE,
              INDEX_COMMENT
          FROM
              information_schema.STATISTICS
          WHERE
              TABLE_SCHEMA = :dbName
              AND TABLE_NAME = :tableName
          """;

      List<Map<String, Object>> indexInfoList = jdbi.withHandle(handle -> handle.createQuery(queryString)
          .bind("dbName", sessionInfo.getUser().extractDbName())
          .bind("tableName", tableName)
          .mapToMap()
          .list());
      return indexInfoList;

    }
    //System.out.println(query.validate().toString());

    return null;
  }
}
