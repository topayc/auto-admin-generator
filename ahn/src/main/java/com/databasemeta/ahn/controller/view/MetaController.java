package com.databasemeta.ahn.controller.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.databasemeta.ahn.dto.DatabaseConnectionInfo;
import com.databasemeta.ahn.service.FileService;
import com.databasemeta.ahn.service.MainService;
import com.databasemeta.ahn.service.SessionManager;
import com.databasemeta.ahn.session.SessionInfo;
import com.databasemeta.ahn.session.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/service")
@io.swagger.v3.oas.annotations.Hidden
public class MetaController {
  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String userName;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${spring.datasource.driver-class-name}")
  private String driverClassName;

  private final MainService mainService;
  private final FileService fileService;
  private final SessionManager sessionManager;

  
  /**
   * 
   * @ModelAttribute 붙은 메소드는 가장 먼저 실행되며 반환값이 널이 아니면, Model 어노테이션에 붙은 이름으로 모델에 추가한다.널을 반환할 경우 는 모델에 추가하지 않는다
   * @param request 메서드 인자로 스프링 부트가 자동 주입해서 호출해준다
   * @return 모델에 추가할 데이타 -> 여기서는 데이타베이스 메타데이터가 세션에 있으면 가져와 반환한다.
   * @throws Exception
   */
  @ModelAttribute("tables")
  public Map<String, ArrayList<Map<String, String>>> tableMap(HttpServletRequest request) throws Exception{
    HttpSession session = request.getSession();
    if (session.getAttribute("sessionInfo") != null){
      return  ((SessionInfo)session.getAttribute("sessionInfo")).getUser().getMetadataTableMap();
    }
    return null;
    
  }

  @GetMapping(value = "/{uuid}") 
  public String index(HttpServletRequest request, Model model) throws Exception  {
    HttpSession session = request.getSession(); 
    SessionInfo sessionInfo = this.sessionManager.checkSession(session);
    
    if (sessionInfo == null){
      return "redirect:/service/create";
    }
    model.addAttribute("tables", sessionInfo.getUser().getMetadataTableMap());

    return "pages/service/index";
  }

  @GetMapping(value = "/readme.md") 
  public String notice( HttpServletRequest request,Model model) throws Exception  {
    HttpSession session = request.getSession(false); 
    SessionInfo sessionInfo = (session != null) ? (SessionInfo) session.getAttribute("sessionInfo") : null;
    
    if (sessionInfo == null){
      return "redirect:/service/create";
    }
    model.addAttribute("tables", sessionInfo.getUser().getMetadataTableMap());
    return "pages/service/readme";
  }


  @GetMapping(value = "/meta/{uuid}") 
  public String databaseMeta(HttpServletRequest request, Model model) throws Exception  {
    HttpSession session = request.getSession(); 
    SessionInfo sessionInfo = this.sessionManager.checkSession(session);

    if (sessionInfo == null){
      return "redirect:/service/create";
    }
    log.info("uri : " + request.getRequestURI());
    //request.setAttribute("tables", sessionInfo.getUser().getMetadataTableMap());
    model.addAttribute("tables", sessionInfo.getUser().getMetadataTableMap());
    return "pages/service/database_meta";
  }

  @GetMapping(value = "/dashboard/{uuid}") 
  public String dashboard(HttpServletRequest request, Model model) throws Exception  {
    log.info("uri : " + request.getRequestURI());
    HttpSession session = request.getSession(); 
    SessionInfo sessionInfo = this.sessionManager.checkSession(session);
    
    if (sessionInfo == null){
      return "redirect:/service/create";
    }
    model.addAttribute("tables", sessionInfo.getUser().getMetadataTableMap());
    return "pages/service/dashboard";
  }

  @GetMapping(value = "/project_info/{uuid}") 
  public String config(HttpServletRequest request, Model model) throws Exception  {
    HttpSession session = request.getSession(); 
    SessionInfo sessionInfo = this.sessionManager.checkSession(session);
    
    if (sessionInfo == null){
      return "redirect:/service/create";
    }
    model.addAttribute("tables", sessionInfo.getUser().getMetadataTableMap());
    return "pages/service/project_info";
  }


  @GetMapping(value = "/create") 
  public String create( 
  HttpServletRequest request,Model model) throws Exception  {

    String uuid = UUID.randomUUID().toString();
    DatabaseConnectionInfo connectionInfo = new DatabaseConnectionInfo();
    connectionInfo.setUuid(uuid);
    connectionInfo.setProjectName("");
    connectionInfo.setUrl(this.url);
    connectionInfo.setUserName(this.userName);
    connectionInfo.setPassword(this.password);
    connectionInfo.setDriverClassName("com.mysql.cj.jdbc.Driver");
    connectionInfo.setCss("bootstrap");
    connectionInfo.setPassword("a98310");
    connectionInfo.setDbms("mysql");
    model.addAttribute("connectionInfo", connectionInfo);
    return "pages/service/create";
  }

  @PostMapping(value = "/create") 
  public String createProject(
      @Valid @ModelAttribute DatabaseConnectionInfo connectionInfo,
      BindingResult bindingResult,
      HttpServletRequest request,Model model) throws Exception  {
        
    if (bindingResult.hasErrors()){
      model.addAttribute("connectionInfo", connectionInfo);
      model.addAttribute("error", true);
      return "pages/service/create";
    }
    //System.out.println("=========================  connection info ");
    //ObjectMapper mapper2 = new ObjectMapper();
    //mapper2.enable(SerializationFeature.INDENT_OUTPUT); 
    //String json = mapper2.writeValueAsString(connectionInfo);
    //System.out.println(json);
    
    HttpSession session = request.getSession(); 
    SessionInfo sessionInfo = (SessionInfo)session.getAttribute("sessionInfo");
    
    if (sessionInfo != null){
      HikariDataSource source =sessionInfo.getDatasource();
      source.close();;
      session.invalidate();
    }
   
    HikariConfig config = new HikariConfig();
  
    config.setJdbcUrl(connectionInfo.getUrl());
    config.setUsername(connectionInfo.getUserName());
    config.setPassword(connectionInfo.getPassword());
    config.setDriverClassName(connectionInfo.getDriverClassName());

    config.setMaximumPoolSize(2);
    config.setMinimumIdle(2);
    config.setConnectionTimeout(30000);
    config.setIdleTimeout(600000);
    config.setMaxLifetime(1800000);

    HikariDataSource dataSource = new HikariDataSource(config);
    //데이티베이스 메타 메이터 테이블 맵 생성
    Map<String, ArrayList<Map<String, String>>> tableMap= this.mainService.getMysqlDatabaseMetaData(dataSource);

    this.fileService.saveDatabaseConnectionInfo(connectionInfo);
    User user = User.builder()
        .projectName(connectionInfo.getProjectName())
        .url(connectionInfo.getUrl())
        .name(connectionInfo.getUserName())
        .metadataTableMap(tableMap)
        .uuid(connectionInfo.getUuid())
        .password(connectionInfo.getPassword())
        .css(connectionInfo.getCss())
        .dbms(connectionInfo.getDbms())
        .driverClassName(connectionInfo.getDriverClassName())
        .build();

    if (this.fileService.saveDatabaseConnectionInfo(connectionInfo));

    sessionInfo = SessionInfo.builder().datasource(dataSource).user(user).build();
    request.getSession().setAttribute("sessionInfo", sessionInfo);

    //테이블 기본 정보와 관련 제약조건, 기본키, 왜래기, 인덱스, 유니크 메타데이터와 병합
    this.mainService.mergeConstraints(sessionInfo, tableMap);
    sessionInfo.getUser().setMetadataTableMap(tableMap);

    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT); 
    String json2 = mapper.writeValueAsString(tableMap);
    System.out.println(json2);
   
    return "redirect:/service/dashboard/" + sessionInfo.getUser().getUuid();
  }

  @GetMapping(value = "/search/{uuid}") 
  public String search(
    @RequestParam(value = "tb") String table,
    HttpServletRequest request, Model model) throws Exception  {
      HttpSession session = request.getSession(false); 
      SessionInfo sessionInfo = (session != null) ? (SessionInfo) session.getAttribute("sessionInfo") : null;
      
      if (sessionInfo == null){
        return "redirect:/service/create";
      }
      
      List<Map<String, Object>> dataMapList = this.mainService.getDataList(request, table);
      
      //전체 테이블 맵리스트를 반환
      model.addAttribute("tables", sessionInfo.getUser().getMetadataTableMap());
      
      //전체 테이블 맵에서 선택한 테이블의 컬럼 메타 정보 맵을 반환
      model.addAttribute("selectedTableColumnList", sessionInfo.getUser().getMetadataTableMap().get(table));
      
      //선택한 테이블 이름을 그대로 반환
      model.addAttribute("selectedTable", sessionInfo.getUser().getMetadataTableMap().get(table));
      model.addAttribute("selectedTable", table);
      model.addAttribute("dataList", dataMapList);
      
      // 해당 테이블 에서 primary 키를 뽑아서 모델에 전달
      List<Map<String, String>> resultList = sessionInfo.getUser().getMetadataTableMap().get(table).stream()
        .filter(m-> m.get("COLUMN_KEY").equalsIgnoreCase("PRI"))
        .collect(Collectors.toList());
      
      //ystem.err.println("primary key : " +  resultList.get(0).get("COLUMN_NAME"));
      if (resultList.size() > 0){
        model.addAttribute("primaryColumn", resultList.get(0).get("COLUMN_NAME"));
      }
      
      List<String> columnHeaderList = sessionInfo.getUser().getMetadataTableMap().get(table)
          .stream().map(map -> map.get("COLUMN_NAME"))
          .collect(Collectors.toList());
      //model.addAttribute("DAT_LIST_TAG",TagGenerator.generateListTag(table, columnHeaderList, dataMapList));
      //System.out.println("DataList Tag ======================");
      //System.out.println(TagGenerator.generateListTag(table, columnHeaderList, dataMapList));
      // System.err.println(columnHeaderList);

      // ObjectMapper mapper = new ObjectMapper();
      // mapper.registerModule(new JavaTimeModule());
      // mapper.enable(SerializationFeature.INDENT_OUTPUT); 
      // String json2 = mapper.writeValueAsString( dataMapList);
      // System.out.println(json2);
      // System.out.println();
      // System.out.println();
      // System.out.println(resultList.get(0).get("COLUMN_NAME"));
      // for (Object key : dataMapList.get(0).keySet()){
      //   System.out.println(key + " : " + key.getClass().getName());
      // }

      return "pages/service/search";
  }

  @GetMapping(value = "/create_join_relation/{uuid}") 
  public String createJoinRelateion( HttpServletRequest request) throws Exception  {
    HttpSession session = request.getSession(false); 
    SessionInfo sessionInfo = (session != null) ? (SessionInfo) session.getAttribute("sessionInfo") : null;
    
    if (sessionInfo == null){
      return "redirect:/service/create";
    }
    return "pages//service/create_join_relation";
  }

  
  @GetMapping(value = "/editor/{uuid}") 
  public String editor( HttpServletRequest request) throws Exception  {
    HttpSession session = request.getSession(false); 
    SessionInfo sessionInfo = (session != null) ? (SessionInfo) session.getAttribute("sessionInfo") : null;
    
    if (sessionInfo == null){
      return "redirect:/service/create";
    }
 
    return "pages/a3maker/index";
  }

  @GetMapping(value = "/meta_field_summary/{uuid}") 
  public String metadataSummary( HttpServletRequest request, Model model) throws Exception  {
    HttpSession session = request.getSession(false); 
    SessionInfo sessionInfo = (session != null) ? (SessionInfo) session.getAttribute("sessionInfo") : null;
    
    if (sessionInfo == null){
      return "redirect:/service/create";
    }
    String key = sessionInfo.getUser().getMetadataTableMap().keySet().stream().findFirst().orElse(null);
    Map<String, String> summaryMap = null;
    
    if (key != null){
      summaryMap = sessionInfo.getUser().getMetadataTableMap().get(key).stream().findFirst().orElse(null);
    }
    model.addAttribute("summaryMap", summaryMap);
    return "pages/service/meta_field_summary";
  }


}
