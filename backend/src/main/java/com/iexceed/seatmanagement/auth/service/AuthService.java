package com.iexceed.seatmanagement.auth.service;

import com.iexceed.seatmanagement.auth.dto.AuthRequest;
import com.iexceed.seatmanagement.auth.dto.AuthResponse;

import javax.security.auth.login.AccountLockedException;

public interface AuthService {
    AuthResponse authenticateUser(AuthRequest authRequest);
}
