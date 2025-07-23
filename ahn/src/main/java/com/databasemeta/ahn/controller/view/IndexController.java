package com.databasemeta.ahn.controller.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.databasemeta.ahn.service.FileService;
import com.databasemeta.ahn.service.MainService;
import com.databasemeta.ahn.service.SessionManager;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {
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

  @GetMapping(value = "/tem") 
  public String index(HttpServletRequest request)  {
    log.info("uri : " + request.getRequestURI());
    return "index";
  }


  @GetMapping(value = "/") 
  public String index(HttpServletRequest request, Model model) throws Exception  {
    // HttpSession session = request.getSession(); 
    // SessionInfo sessionInfo = this.sessionManager.checkSession(session);
    
    // if (sessionInfo == null){
    //   return "redirect:/create";
    // }
    // model.addAttribute("tables", sessionInfo.getUser().getTableMap());

    return "pages/front/index";
  }

  @GetMapping(value = "/se") 
  public String se(HttpServletRequest request, Model model) throws Exception  {
    return "pages/se";
  }

  @GetMapping(value = "/tw") 
  public String tw(HttpServletRequest request, Model model) throws Exception  {
    return "tw/index";
  }
  

  @GetMapping(value = "/tw2") 
  public String t2(HttpServletRequest request, Model model) throws Exception  {
    return "tw/index2";
  }
  
}
