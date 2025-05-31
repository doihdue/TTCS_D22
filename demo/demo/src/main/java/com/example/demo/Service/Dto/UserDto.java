package com.example.demo.Service.Dto;

import com.example.demo.Model.User;

public class UserDto extends User {

    private String confirmPassword;

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
