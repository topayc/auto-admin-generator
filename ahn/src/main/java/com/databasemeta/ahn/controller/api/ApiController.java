package com.databasemeta.ahn.controller.api;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.databasemeta.ahn.dto.ApiResponse;
import com.databasemeta.ahn.service.MainService;
import com.databasemeta.ahn.service.SessionManager;
import com.databasemeta.ahn.session.SessionInfo;
import com.databasemeta.ahn.util.TagGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiController {
  private final MainService mainService;
  private final SessionManager sessionManager;

  
  @GetMapping(value = "/") 
  public ResponseEntity<?> main(HttpServletRequest request, HttpServletResponse response ) throws Exception{
    HttpSession session = request.getSession(); 
    SessionInfo sessionInfo = this.sessionManager.checkSession(session);
    if (sessionInfo == null){
      org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
      headers.setLocation(URI.create("http://localhost:8080/service/"));
      return new ResponseEntity<Void>(headers, HttpStatus.FOUND);
    }
    return ResponseEntity.ok().body(this.mainService.getMysqlDatabaseMetaData(sessionInfo.getDatasource()));
  }

  @GetMapping(value = "/constraint") 
  public ResponseEntity<?> constraint(String table, String constraint, HttpServletRequest request ,  HttpServletResponse response) throws Exception{
    HttpSession session = request.getSession(); 
    SessionInfo sessionInfo = this.sessionManager.checkSession(session);
    if (sessionInfo == null){
      org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
      headers.setLocation(URI.create("http://localhost:8080/service/create"));
      return new ResponseEntity<Void>(headers, HttpStatus.FOUND);
    }
    List<Map<String, Object>> constraintList = this.mainService.getConstraintData(sessionInfo,table, constraint);
    String html = TagGenerator.generateListTag(table, constraintList);
    return ResponseEntity.ok().body(html);
  }

  @DeleteMapping(value = "/items/{itemId}") 
  public ResponseEntity<?> removeItem(
    @RequestParam("tb") String table, @PathVariable("itemId") String itemId, 
    HttpServletRequest request ,  
    HttpServletResponse response) throws Exception{

    HttpSession session = request.getSession(); 
    SessionInfo sessionInfo = this.sessionManager.checkSession(session);
    if (sessionInfo == null){
      org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
      headers.setLocation(URI.create("http://localhost:8080/service/create"));
      return new ResponseEntity<Void>(headers, HttpStatus.FOUND);
    }
    System.out.println("삭제 테이블 : " + table);
    System.out.println("삭제 아이디 : " + itemId);

    ApiResponse<String> apiResponse = ApiResponse.<String>builder()
      .status("SUCCESS")
      .message("요청이 성공적으로 처리되었습니다.")
      .data("삭제 요청이 성공했습니다.")
      .build();
    return ResponseEntity.ok().body(apiResponse);
  }
}
