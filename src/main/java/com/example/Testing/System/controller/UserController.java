package com.example.Testing.System.controller;


import com.example.Testing.System.constant.HttpStatuses;
import com.example.Testing.System.dto.user.ChangePasswordRequestDto;
import com.example.Testing.System.dto.user.UserProfileResponseDto;
import com.example.Testing.System.model.User;
import com.example.Testing.System.repository.UserRepository;
import com.example.Testing.System.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for user profile and password management")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Get current user profile", description = "Returns the profile of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = UserProfileResponseDto.class))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })

    @GetMapping
    public ResponseEntity<UserProfileResponseDto> getCurrentUser(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfileResponseDto response = new UserProfileResponseDto(user.getEmail(), user.getFullName());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Change password", description = "Allows an authenticated user to change their password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDto request,
                                                 Principal principal) {
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Password changed successfully");
    }


}
