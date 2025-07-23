package com.databasemeta.ahn.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DatabaseConnectionInfo {
  
  @NotBlank(message = "필수 입력값입니다.")
  private String projectName;

  @NotBlank(message = "필수 입력값입니다.")
  private String url;

  @NotBlank(message = "필수 입력값입니다.")
  private String userName; 

  @NotBlank(message = "필수 입력값입니다.")
  private String dbms;

  @NotBlank(message = "필수 입력값입니다.")
  private String password; 

  @NotBlank(message = "필수 입력값입니다.")
  private String driverClassName; 

  @NotBlank(message = "필수 입력값입니다.")
  private String css; 

  @NotBlank(message = "필수 입력값입니다.")
  private String uuid;
}
