package com.example.Testing.System.controller;

import com.example.Testing.System.constant.HttpStatuses;
import com.example.Testing.System.dto.user.AuthRequestDto;
import com.example.Testing.System.dto.user.AuthResponseDTO;
import com.example.Testing.System.dto.user.SignupRequestDto;
import com.example.Testing.System.model.User;
import com.example.Testing.System.repository.UserRepository;
import com.example.Testing.System.security.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login and registration")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Login", description = "Authenticate a user by email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDto request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtUtil.generateToken(request.getEmail());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @Operation(summary = "Signup", description = "Register a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK,
                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signup(@RequestBody SignupRequestDto request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new AuthResponseDTO("Passwords do not match"));
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new AuthResponseDTO("Email already in use"));
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

}
