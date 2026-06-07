package com.iexceed.seatmanagement.auth.service.impl;

import com.iexceed.seatmanagement.auth.dto.AuthRequest;
import com.iexceed.seatmanagement.auth.dto.AuthResponse;
import com.iexceed.seatmanagement.auth.exceptions.AccountDisabledException;
import com.iexceed.seatmanagement.auth.exceptions.InvalidCredentialsException;
import com.iexceed.seatmanagement.auth.service.AuthService;
import com.iexceed.seatmanagement.employees.entity.Employee;
import com.iexceed.seatmanagement.employees.repository.EmployeeRepository;
import com.iexceed.seatmanagement.roles.entity.EmployeeRole;
import com.iexceed.seatmanagement.roles.enums.RoleCode;
import com.iexceed.seatmanagement.roles.repository.EmployeeRoleRepository;
import com.iexceed.seatmanagement.security.jwt.JwtUtil;
import com.iexceed.seatmanagement.users.entity.SystemUser;
import com.iexceed.seatmanagement.users.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.iexceed.seatmanagement.auth.exceptions.AccountLockedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SystemUserRepository systemUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse authenticateUser(AuthRequest authRequest) {
        // Find the user by username
        SystemUser systemUser = systemUserRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + authRequest.getUsername()));
        // Validate the user
        if (!Boolean.TRUE.equals(systemUser.getEnabled())) {
            throw new AccountDisabledException("User account is disabled");
        }
        //Check whether the account is locked
        if (Boolean.TRUE.equals(systemUser.getAccountLocked())) {
            throw new AccountLockedException(
                    "Account is locked. Contact administrator."
            );
        }
        //Validate the password
        boolean passwordMatches = passwordEncoder.matches(authRequest.getPassword(), systemUser.getPasswordHash());
        if (!passwordMatches) {
            Integer attempts = Optional.ofNullable(
                    systemUser.getFailedLoginAttempts()
            ).orElse(0);
            attempts++;
            systemUser.setFailedLoginAttempts(attempts);
            if (attempts >= 5) {
                systemUser.setAccountLocked(true);
                systemUser.setLockedAt(LocalDateTime.now());
            }
            systemUserRepository.save(systemUser);
            throw new InvalidCredentialsException("Invalid password");
        }
        //save login details
        systemUser.setFailedLoginAttempts(0);
        systemUser.setAccountLocked(false);
        systemUser.setLockedAt(null);
        systemUser.setLastLoginAt(LocalDateTime.now());

        systemUserRepository.save(systemUser);
        //Lookup role details
        List<String> roles = employeeRoleRepository.findByEmployeeIdAndActiveTrue(systemUser.getEmployeeId())
                .stream()
                .map(EmployeeRole::getRoleCode)
                .map(RoleCode::name)
                .toList();
        //Lookup employee
        Employee employee = employeeRepository.findById(systemUser.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found for user: " + systemUser.getUsername()));
        //Generate JWT token
        String token = jwtUtil.generateToken(systemUser.getUsername(), roles);
        return AuthResponse.builder()
                .email(employee.getEmail())
                .displayName(employee.getDisplayName())
                .employeeCode(employee.getEmployeeCode())
                .token(token)
                .build();
    }
}
