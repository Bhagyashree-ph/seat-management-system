package com.iexceed.seatmanagement.audit.service.impl;

import com.iexceed.seatmanagement.audit.dto.response.AuditLogResponse;
import com.iexceed.seatmanagement.audit.entity.AuditLog;
import com.iexceed.seatmanagement.audit.entity.FieldChange;
import com.iexceed.seatmanagement.audit.enums.AuditAction;
import com.iexceed.seatmanagement.audit.enums.EntityType;
import com.iexceed.seatmanagement.audit.mapper.AuditMapper;
import com.iexceed.seatmanagement.audit.repository.AuditLogRepository;
import com.iexceed.seatmanagement.audit.service.AuditService;
import com.iexceed.seatmanagement.security.util.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(EntityType entityType, String entityId, AuditAction action, Map<String, FieldChange> changes) {
        try {
            String userEmail = CurrentUserUtil.getCurrentUserEmail();
            AuditLog auditLog = AuditLog.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .action(action)
                    .userEmail(userEmail)
                    .changes(changes)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);

            log.debug("Audit log created. EntityType={}, EntityId={}, Action={}, User={}", entityType, entityId, action, userEmail);

        } catch (Exception ex) {
            log.error("Failed to create audit log. EntityType={}, EntityId={}, Action={}", entityType,  entityId, action, ex);
        }
    }

    @Override
    public Page<AuditLogResponse> getAuditLogs(EntityType entityType, String entityId, String userEmail, Pageable pageable) {
        Page<AuditLog> auditLogs;

        if (entityType != null && entityId != null) {
            auditLogs = auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable);
        } else if (entityType != null) {
            auditLogs = auditLogRepository.findByEntityType(entityType, pageable);
        } else if (userEmail != null) {
            auditLogs = auditLogRepository.findByUserEmail(userEmail, pageable);
        } else {
            auditLogs = auditLogRepository.findAll(pageable);
        }

        return auditLogs.map(AuditMapper::toResponse);
    }

    @Override
    public List<AuditLogResponse> getEntityAuditHistory(EntityType entityType, String entityId) {

        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType,entityId)
                .stream()
                .map(AuditMapper::toResponse)
                .toList();
    }
}
