package com.iexceed.seatmanagement.auth.oauth.microsoft;

import com.iexceed.seatmanagement.auth.dto.AuthResponse;
import com.iexceed.seatmanagement.auth.dto.CurrentUserResponse;
import com.iexceed.seatmanagement.auth.oauth.microsoft.config.MicrosoftOAuthProperties;
import com.iexceed.seatmanagement.employees.entity.Employee;
import com.iexceed.seatmanagement.employees.repository.EmployeeRepository;
import com.iexceed.seatmanagement.roles.enums.RoleCode;
import com.iexceed.seatmanagement.roles.service.EmployeeRoleService;
import com.iexceed.seatmanagement.security.jwt.JwtUtil;
import com.iexceed.seatmanagement.auth.exceptions.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MicrosoftOAuthService {

    private final MicrosoftGraphClient microsoftGraphClient;
    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;
    private final MicrosoftOAuthProperties microsoftOAuthProperties;
    private final EmployeeRoleService employeeRoleService;

    public AuthResponse authenticate(String code) {

        MicrosoftTokenResponse tokenResponse =
                microsoftGraphClient.exchangeCodeForToken(code);

        MicrosoftUserProfile profile = microsoftGraphClient.getUserProfile(
                tokenResponse.getAccess_token()
        );

        String email = profile.getMail() != null ? profile.getMail() : profile.getUserPrincipalName();

        Employee employeeEntity = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("User with email " + email + " not found in the system"));

        if (!Boolean.TRUE.equals(employeeEntity.getActive())) {
            throw new EmployeeInactiveException("Employee is inactive");
        }

        if (Boolean.TRUE.equals(employeeEntity.getDeleted())) {
            throw new EmployeeDeletedException("Employee is deleted");
        }

        employeeRoleService.assignDefaultRole(employeeEntity.getId());
        List<String> roles = employeeRoleService.getEmployeeRoles(employeeEntity.getId())
                .stream()
                .map(RoleCode::name)
                .toList();
        String token = jwtUtil.generateToken(employeeEntity.getEmployeeCode(), employeeEntity.getEmail(), roles);

        return AuthResponse.builder()
                .token(token)
                .employeeCode(employeeEntity.getEmployeeCode())
                .email(employeeEntity.getEmail())
                .displayName(profile.getDisplayName())
                .build();
    }

    public String getAuthorizationUrl() {

        return "https://login.microsoftonline.com/"
                + microsoftOAuthProperties.getTenantId()
                + "/oauth2/v2.0/authorize"
                + "?client_id=" + microsoftOAuthProperties.getClientId()
                + "&response_type=code"
                + "&redirect_uri=" + microsoftOAuthProperties.getRedirectUri()
                + "&response_mode=query"
                + "&scope=openid profile email User.Read";
    }

    public CurrentUserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("User with email " + email + " not found in the system"));
        return CurrentUserResponse.builder()
                .employeeCode(employee.getEmployeeCode())
                .email(employee.getEmail())
                .displayName(employee.getDisplayName())
                .build();
    }

}