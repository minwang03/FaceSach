package com.example.facesach.model;

public class ApiResponse<T> {
    private String message;
    private T data;

    public String getMessage() { return message; }
    public T getData() { return data; }
}

