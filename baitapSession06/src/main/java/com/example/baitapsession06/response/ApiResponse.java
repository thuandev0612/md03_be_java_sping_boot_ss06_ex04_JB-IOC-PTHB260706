package com.example.baitapsession06.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final Boolean success;
    private final String message;
    private final T data;
}
