package com.databasemeta.ahn.controller.view.admingen;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.databasemeta.ahn.service.SessionManager;
import com.databasemeta.ahn.session.SessionInfo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FormController {
  
  public final SessionManager sessionManager;
  
  @GetMapping("get_form")
  public ResponseEntity<?> form(String tableName, HttpServletRequest request) throws Exception{
    SessionInfo sessionInfo = this.sessionManager.checkSession(request.getSession());
  
    return ResponseEntity.ok().body(
      sessionInfo.getUser().getMetadataTableMap().get(tableName)
        .stream()
        .map(m ->String.valueOf(m.get("COL_WRAPPED_STYLED_CREATE_TAG"))).collect(Collectors.joining("")));  
    
  }
}
