package com.iexceed.seatmanagement.audit.controller;

import com.iexceed.seatmanagement.audit.dto.response.AuditLogResponse;
import com.iexceed.seatmanagement.audit.enums.EntityType;
import com.iexceed.seatmanagement.audit.service.AuditService;
import com.iexceed.seatmanagement.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ApiResponse<Page<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) EntityType entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String userEmail,
            Pageable pageable) {

        return ApiResponse.<Page<AuditLogResponse>>builder()
                .success(true)
                .data(auditService.getAuditLogs(entityType, entityId, userEmail, pageable))
                .message("Audit logs retrieved successfully")
                .build();
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ApiResponse<List<AuditLogResponse>> getEntityAuditHistory(
            @PathVariable EntityType entityType,
            @PathVariable String entityId) {

        return ApiResponse.<List<AuditLogResponse>>builder()
                .success(true)
                .data(auditService.getEntityAuditHistory(entityType, entityId))
                .message("Audit history for entity retrieved successfully")
                .build();
    }
}