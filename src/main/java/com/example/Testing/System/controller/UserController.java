package com.example.Testing.System.controller;


import com.example.Testing.System.dto.user.ChangePasswordRequestDto;
import com.example.Testing.System.dto.user.UserProfileResponseDto;
import com.example.Testing.System.model.User;
import com.example.Testing.System.repository.UserRepository;
import com.example.Testing.System.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getCurrentUser(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfileResponseDto response = new UserProfileResponseDto(user.getEmail(), user.getFullName());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDto request,
                                                 Principal principal) {
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Password changed successfully");
    }


}
