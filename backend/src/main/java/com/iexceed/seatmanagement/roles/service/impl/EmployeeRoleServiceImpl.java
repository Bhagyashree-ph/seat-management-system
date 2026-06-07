package com.iexceed.seatmanagement.roles.service.impl;

import com.iexceed.seatmanagement.employees.entity.Employee;
import com.iexceed.seatmanagement.employees.repository.EmployeeRepository;
import com.iexceed.seatmanagement.roles.entity.EmployeeRole;
import com.iexceed.seatmanagement.roles.enums.RoleCode;
import com.iexceed.seatmanagement.roles.repository.EmployeeRoleRepository;
import com.iexceed.seatmanagement.roles.repository.RoleRepository;
import com.iexceed.seatmanagement.roles.service.EmployeeRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeRoleServiceImpl implements EmployeeRoleService {

    private final EmployeeRoleRepository employeeRoleRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void assignRole(String employeeId,RoleCode roleCode,String assignedBy) {
        roleRepository.findByCode(roleCode)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found: "+roleCode
                        ));

        if(employeeRoleRepository
                .existsByEmployeeIdAndRoleCodeAndActiveTrue(
                        employeeId,
                        roleCode
                )) {
            return;
        }

        EmployeeRole employeeRole=EmployeeRole.builder()
                .employeeId(employeeId)
                .roleCode(roleCode)
                .active(true)
                .assignedAt(Instant.now())
                .assignedBy(assignedBy)
                .build();

        employeeRoleRepository.save(employeeRole);
    }

    @Override
    public List<RoleCode> getEmployeeRoles(String employeeId) {
        return employeeRoleRepository
                .findByEmployeeIdAndActiveTrue(employeeId)
                .stream()
                .map(EmployeeRole::getRoleCode)
                .toList();
    }

    @Override
    public boolean hasRole(String employeeId,RoleCode roleCode) {
        return employeeRoleRepository
                .existsByEmployeeIdAndRoleCodeAndActiveTrue(
                        employeeId,
                        roleCode
                );
    }

    @Override
    public void assignDefaultRole(String employeeId) {
        if(employeeRoleRepository.findByEmployeeIdAndActiveTrue(employeeId)
                .isEmpty()) {
            assignRole(
                    employeeId,
                    RoleCode.EMPLOYEE,
                    "SYSTEM"
            );
        }
    }

    @Override
    public List<RoleCode> getCurrentUserRoles() {

        String email=(String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Employee employee=employeeRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found: "+email
                        ));

        return getEmployeeRoles(employee.getId());
    }
}