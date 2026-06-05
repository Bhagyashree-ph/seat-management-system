package com.iexceed.seatmanagement.employees.schedulers;

import com.iexceed.seatmanagement.employees.service.EmployeeSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeSyncScheduler {

    private final EmployeeSyncService employeeSyncService;

    @Scheduled(
            cron = "${employee.schedule.sync.cron:0 0 2 * * *}",
            zone = "${employee.schedule.sync.zone:Asia/Kolkata}"
    )
    public void scheduleEmployeeSync() {
        log.info("Triggering scheduled employee sync");
        employeeSyncService.syncEmployees();
    }
}
