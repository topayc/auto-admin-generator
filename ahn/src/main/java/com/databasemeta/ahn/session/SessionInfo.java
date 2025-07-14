package com.databasemeta.ahn.session;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class SessionInfo {
  private HikariDataSource datasource;
  private User user;
}
