package com.example.facesach.model;

public class ApiResponse<T> {
    private String message;
    private T data;
    private boolean success;

    public String getMessage() { return message; }
    public T getData() { return data; }

    public boolean isSuccess() {
        return success;
    }
}

