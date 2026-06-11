package com.iexceed.seatmanagement.audit.mapper;

import com.iexceed.seatmanagement.audit.dto.response.AuditLogResponse;
import com.iexceed.seatmanagement.audit.entity.AuditLog;

public final class AuditMapper {

    private AuditMapper() {
    }

    public static AuditLogResponse toResponse(
            AuditLog auditLog) {

        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .action(auditLog.getAction())
                .userEmail(auditLog.getUserEmail())
                .changes(auditLog.getChanges())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}