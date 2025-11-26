package com.yourcompany.hrms.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper<T> {
    
    private boolean success;
    private String message;
    private T data;
    
    public static <T> ResponseWrapper<T> success(T data) {
        return ResponseWrapper.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    public static <T> ResponseWrapper<T> success(String message, T data) {
        return ResponseWrapper.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> ResponseWrapper<T> success(String message) {
        return ResponseWrapper.<T>builder()
                .success(true)
                .message(message)
                .build();
    }
    
    public static <T> ResponseWrapper<T> error(String message) {
        return ResponseWrapper.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}

