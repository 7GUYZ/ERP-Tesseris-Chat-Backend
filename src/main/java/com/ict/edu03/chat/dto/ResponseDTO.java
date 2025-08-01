package com.ict.edu03.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {
    
    private int resultCode;
    private String resultMessage;
    T data;

    public static ResponseDTO<?> createErrorResponse(int code, String message) {
        return ResponseDTO.builder()
                .resultCode(code)
                .resultMessage(message)
                .build();
    }

    public static <T> ResponseDTO<T> createSuccessResponse(String message, T data) {
        return ResponseDTO.<T>builder()
                .resultCode(200)
                .data(data)
                .resultMessage(message)
                .build();
    }

}
