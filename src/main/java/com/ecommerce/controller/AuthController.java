package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.dto.user.AuthResponseDto;
import com.ecommerce.dto.user.LoginRequestDto;
import com.ecommerce.dto.user.RefreshTokenRequestDto;
import com.ecommerce.dto.user.UserCreateDto;
import com.ecommerce.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Endpoints for auth management")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account and return auth tokens")
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(
            @Valid @RequestBody UserCreateDto dto
    ) {
        var response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(response, HttpStatus.CREATED.value(), "User registered successfully"));
    }

    /**
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return access and refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(
            @Valid @RequestBody LoginRequestDto dto) {
        var response = authService.login(dto);
        return ResponseEntity.ok(new ApiResponse<>(response, HttpStatus.OK.value(), "Login successful"));
    }

    /**
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Use refresh token to get a new access token")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(
            @Valid @RequestBody RefreshTokenRequestDto dto) {
        var response = authService.refreshAccessToken(dto.getRefreshToken());
        return ResponseEntity.ok(new ApiResponse<>(response, HttpStatus.OK.value(), "Token refreshed"));
    }

    /**
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidate the current user's tokens (if applicable)")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // С JWT нет сессии, но можно добавить blacklist в Redis если нужно
        return ResponseEntity.ok(new ApiResponse<>(null, HttpStatus.OK.value(), "Logged out successfully"));
    }

    /**
     * GET /api/v1/auth/me
     * Получить текущего пользователя
     * Authentication автоматически инжектируется Spring Security
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieve the email of the currently authenticated user")
    public ResponseEntity<ApiResponse<String>> getCurrentUser(Authentication authentication) {
        // authentication.getName() вернёт email пользователя
        String email = authentication.getName();
        return ResponseEntity.ok(new ApiResponse<>(email, HttpStatus.OK.value(), "Current user email"));
    }
}