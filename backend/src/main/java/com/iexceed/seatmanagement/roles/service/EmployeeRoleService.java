package com.iexceed.seatmanagement.roles.service;

import com.iexceed.seatmanagement.roles.enums.RoleCode;

import java.util.List;

public interface EmployeeRoleService {

    void assignRole(String employeeId, RoleCode roleCode, String assignedBy);

    List<RoleCode> getEmployeeRoles(String employeeId);

    boolean hasRole(String employeeId, RoleCode roleCode);

    void assignDefaultRole(String employeeId);

    List<RoleCode> getCurrentUserRoles();
}