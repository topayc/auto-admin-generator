package com.databasemeta.ahn.dto;

import lombok.Builder;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class ApiResponse <T>{
    private String status;
    private String message;
    private T data;

    @Builder
    public ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
