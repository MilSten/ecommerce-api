package com.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private T data;
    private int status;
    private String message;

    public ApiResponse(T data, int status) {
        this.data = data;
        this.status = status;
    }

    public ApiResponse(T data, int status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }
}