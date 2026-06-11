package com.iexceed.seatmanagement.audit.dto.response;

import com.iexceed.seatmanagement.audit.entity.FieldChange;
import com.iexceed.seatmanagement.audit.enums.AuditAction;
import com.iexceed.seatmanagement.audit.enums.EntityType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class AuditLogResponse {

    private String id;

    private EntityType entityType;

    private String entityId;

    private AuditAction action;

    private String userEmail;

    private Map<String, FieldChange> changes;

    private LocalDateTime timestamp;
}