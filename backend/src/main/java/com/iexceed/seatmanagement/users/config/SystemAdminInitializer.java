package com.iexceed.seatmanagement.users.config;

import com.iexceed.seatmanagement.users.service.SystemAdminProvisioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemAdminInitializer implements CommandLineRunner {

    private final SystemAdminProvisioningService provisioningService;

    @Override
    public void run(String... args) throws Exception {
        provisioningService.provisionSystemAdmins();
    }
}