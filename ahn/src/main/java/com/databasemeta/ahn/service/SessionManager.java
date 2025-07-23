package com.databasemeta.ahn.service;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.databasemeta.ahn.dto.DatabaseConnectionInfo;
import com.databasemeta.ahn.session.SessionInfo;
import com.databasemeta.ahn.session.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionManager {

  private ApplicationContext applicationContext;
  private final FileService fileService;

   public  SessionInfo checkSession(HttpSession session) throws Exception{
    SessionInfo sessionInfo = (session != null) ? (SessionInfo) session.getAttribute("sessionInfo") : null;
    if (sessionInfo != null) {
      return sessionInfo;
    } else {
      DatabaseConnectionInfo connectionInfo = this.fileService.readDatabaseConnectionInfo();
      if (connectionInfo != null){
        HikariConfig config = new HikariConfig();
        System.out.println(connectionInfo.getUrl());
      
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
        MainService mainService = this.applicationContext.getBean(MainService.class);
        
        Map<String, ArrayList<Map<String, String>>> tableMap= mainService.getMysqlDatabaseMetaData(dataSource);
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
                      .driverClassName(connectionInfo.getDriverClassName()).build();
        
        sessionInfo = SessionInfo.builder().datasource(dataSource).user(user).build();
        session.setAttribute("sessionInfo", sessionInfo);
        return sessionInfo;
      }
      return null;
    }
   
  }
}
