package com.example.Testing.System.dto.user;


import lombok.Data;

@Data
public class SignupRequestDto {
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
}
