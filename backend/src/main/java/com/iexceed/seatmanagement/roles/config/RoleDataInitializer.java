package com.iexceed.seatmanagement.roles.config;

import com.iexceed.seatmanagement.roles.entity.Role;
import com.iexceed.seatmanagement.roles.enums.RoleCode;
import com.iexceed.seatmanagement.roles.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        createRole(RoleCode.SUPER_ADMIN,"Super Admin");
        createRole(RoleCode.HR_ADMIN,"HR Admin");
        createRole(RoleCode.MANAGER,"Manager");
        createRole(RoleCode.EMPLOYEE,"Employee");
    }

    private void createRole(RoleCode roleCode,String name) {
        if(roleRepository.findByCode(roleCode).isPresent()) {
            return;
        }

        Role role=Role.builder()
                .code(roleCode.name())
                .name(name)
                .systemRole(true)
                .active(true)
                .build();

        roleRepository.save(role);
    }
}