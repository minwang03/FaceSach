package com.example.facesach.model;

public class ChangePasswordRequest {
    private String email;
    private String newPassword;

    public ChangePasswordRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }

}

