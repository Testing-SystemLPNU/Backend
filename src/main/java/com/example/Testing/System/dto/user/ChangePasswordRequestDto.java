package com.example.Testing.System.dto.user;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}
