package com.iexceed.seatmanagement.users.service.impl;

import com.iexceed.seatmanagement.employees.entity.Employee;
import com.iexceed.seatmanagement.employees.enums.EmployeeSource;
import com.iexceed.seatmanagement.employees.repository.EmployeeRepository;
import com.iexceed.seatmanagement.roles.entity.EmployeeRole;
import com.iexceed.seatmanagement.roles.enums.RoleCode;
import com.iexceed.seatmanagement.roles.repository.EmployeeRoleRepository;
import com.iexceed.seatmanagement.users.config.SystemAdminConfig;
import com.iexceed.seatmanagement.users.config.SystemAdminLoader;
import com.iexceed.seatmanagement.users.entity.SystemUser;
import com.iexceed.seatmanagement.users.repository.SystemUserRepository;
import com.iexceed.seatmanagement.users.service.SystemAdminProvisioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemAdminProvisioningServiceImpl implements SystemAdminProvisioningService {

    private final SystemAdminLoader systemAdminLoader;

    private final EmployeeRepository employeeRepository;
    private final SystemUserRepository systemUserRepository;
    private final EmployeeRoleRepository employeeRoleRepository;

    private final PasswordEncoder passwordEncoder;

    public void provisionSystemAdmins() throws Exception {

        List<SystemAdminConfig> admins =
                systemAdminLoader.loadSystemAdmins();

        for (SystemAdminConfig admin : admins) {

            Employee employee =
                    createEmployeeIfRequired(admin);

            createSystemUserIfRequired(admin, employee);

            assignSuperAdminRoleIfRequired(employee);
        }
    }

    private Employee createEmployeeIfRequired(
            SystemAdminConfig config) {

        return employeeRepository
                .findByEmployeeCode(config.getEmployeeCode())
                .orElseGet(() -> {

                    Employee employee = Employee.builder()
                            .employeeCode(config.getEmployeeCode())
                            .displayName(config.getDisplayName())
                            .email(config.getEmail())
                            .source(EmployeeSource.SYSTEM)
                            .active(true)
                            .deleted(false)
                            .build();

                    return employeeRepository.save(employee);
                });
    }

    private void createSystemUserIfRequired(
            SystemAdminConfig config,
            Employee employee) {

        if(systemUserRepository.existsByUsername(
                config.getUsername())) {
            return;
        }

        SystemUser systemUser =
                SystemUser.builder()
                        .employeeId(employee.getId())
                        .username(config.getUsername())
                        .passwordHash(
                                passwordEncoder.encode(
                                        config.getPassword()
                                )
                        )
                        .enabled(true)
                        .failedLoginAttempts(0)
                        .accountLocked(false)
                        .failedLoginAttempts(0)
                        .enabled(true)
                        .build();

        systemUserRepository.save(systemUser);
    }

    private void assignSuperAdminRoleIfRequired(
            Employee employee) {

        boolean exists =
                employeeRoleRepository
                        .existsByEmployeeIdAndRoleCode(
                                employee.getId(),
                                RoleCode.SUPER_ADMIN
                        );

        if(exists) {
            return;
        }

        EmployeeRole role =
                EmployeeRole.builder()
                        .employeeId(employee.getId())
                        .roleCode(RoleCode.SUPER_ADMIN)
                        .active(true)
                        .assignedBy("SYSTEM")
                        .assignedAt(Instant.now())
                        .build();

        employeeRoleRepository.save(role);
    }
}
