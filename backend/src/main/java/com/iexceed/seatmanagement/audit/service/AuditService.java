package com.iexceed.seatmanagement.audit.service;

import com.iexceed.seatmanagement.audit.dto.response.AuditLogResponse;
import com.iexceed.seatmanagement.audit.entity.FieldChange;
import com.iexceed.seatmanagement.audit.enums.AuditAction;
import com.iexceed.seatmanagement.audit.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AuditService {

    void log(
            EntityType entityType, String entityId,
            AuditAction action,
            Map<String, FieldChange> changes
    );

    Page<AuditLogResponse> getAuditLogs(
            EntityType entityType, String entityId,
            String userEmail, Pageable pageable
    );

    List<AuditLogResponse> getEntityAuditHistory(
            EntityType entityType, String entityId
    );
}