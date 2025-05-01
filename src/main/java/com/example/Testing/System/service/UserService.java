package com.example.Testing.System.service;

import com.example.Testing.System.dto.user.ChangePasswordRequestDto;
import com.example.Testing.System.model.User;

public interface UserService {
    void changePassword(String email, ChangePasswordRequestDto request);
    User findByEmail(String email);

}
