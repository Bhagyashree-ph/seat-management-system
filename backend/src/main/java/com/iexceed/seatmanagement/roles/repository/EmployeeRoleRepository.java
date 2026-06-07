package com.iexceed.seatmanagement.roles.repository;

import com.iexceed.seatmanagement.roles.entity.EmployeeRole;
import com.iexceed.seatmanagement.roles.enums.RoleCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmployeeRoleRepository
        extends MongoRepository<EmployeeRole, String> {

    List<EmployeeRole> findByEmployeeIdAndActiveTrue(String employeeId);

    boolean existsByEmployeeIdAndRoleCode(String employeeId, RoleCode roleCode);

    boolean existsByEmployeeIdAndRoleCodeAndActiveTrue(String employeeId, RoleCode roleCode);
}