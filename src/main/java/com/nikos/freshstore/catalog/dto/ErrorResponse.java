package com.nikos.freshstore.catalog.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ErrorResponse {

    private int status;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;

    public static ErrorResponse of(int status, String message) {
        return of(status, message, List.of());
    }

    public static ErrorResponse of(int status, String message, List<String> details) {
        ErrorResponse response = new ErrorResponse();
        response.setStatus(status);
        response.setMessage(message);
        response.setDetails(details);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}