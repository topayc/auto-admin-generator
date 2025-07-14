package com.databasemeta.ahn.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.databasemeta.ahn.dto.DatabaseConnectionInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;

@Service
public class FileService {
private static final String FILE_PATH = "db_con_info.json";
    
private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean saveDatabaseConnectionInfo(DatabaseConnectionInfo connectionInfo) throws IOException  {
      File file = new File(FILE_PATH);

      if (file.exists()) {
          file.delete();
      }
      objectMapper.writeValue(file, connectionInfo);
      return true;
    }

    public DatabaseConnectionInfo readDatabaseConnectionInfo() throws IOException{
      
      Path path = Path.of(FILE_PATH);
      String jsonString = null;
      if (Files.exists(path)) {
        jsonString = Files.readString(path);
        return objectMapper.readValue(jsonString, DatabaseConnectionInfo.class);
      } else {
        return null;
      }
    }

    @PreDestroy
    public void clean(){
      File file = new File(FILE_PATH);

      if (file.exists()) {
          file.delete();
      }
    }
}
