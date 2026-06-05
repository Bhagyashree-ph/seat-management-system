package com.iexceed.seatmanagement.employees.controller;

import com.iexceed.seatmanagement.employees.service.EmployeeSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test/zohoSync")
@RequiredArgsConstructor
public class EmployeeSyncController {

    private final EmployeeSyncService employeeSyncService;

    @GetMapping("/sync")
    public ResponseEntity<String> syncEmployees() {
        employeeSyncService.syncEmployees();
        return ResponseEntity.ok("Employee sync started in background. Check logs for progress.");
    }
}