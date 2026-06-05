package com.iexceed.seatmanagement.common.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/user/me")
    public String currentUser(
            Authentication authentication
    ) {

        return "Current User: "
                + authentication.getName();
    }
}