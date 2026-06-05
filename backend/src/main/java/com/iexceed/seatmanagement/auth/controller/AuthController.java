package com.iexceed.seatmanagement.auth.controller;

import com.iexceed.seatmanagement.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    @GetMapping("/auth/token")
    public String generateToken(
            @RequestParam String username
    ) {

        return jwtUtil.generateToken(username);
    }
}