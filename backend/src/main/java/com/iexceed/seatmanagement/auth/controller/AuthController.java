package com.iexceed.seatmanagement.auth.controller;

import com.iexceed.seatmanagement.auth.dto.AuthResponse;
import com.iexceed.seatmanagement.auth.dto.CurrentUserResponse;
import com.iexceed.seatmanagement.auth.oauth.microsoft.MicrosoftOAuthService;
import com.iexceed.seatmanagement.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MicrosoftOAuthService microsoftOAuthService;

    @GetMapping("/microsoft/login")
    public ApiResponse<String> microsoftLogin() {

        return ApiResponse.<String>builder()
                .success(true)
                .message("Microsoft login URL generated successfully")
                .data(microsoftOAuthService.getAuthorizationUrl())
                .build();
    }

    @GetMapping("/microsoft/callback")
    public ApiResponse<AuthResponse> microsoftCallback(
            @RequestParam String code
    ) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Microsoft authentication successful")
                .data(microsoftOAuthService.authenticate(code))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> getCurrentUser() {

        return ApiResponse.<CurrentUserResponse>builder()
                .success(true)
                .message("Current user fetched successfully")
                .data(microsoftOAuthService.getCurrentUser())
                .build();
    }
}