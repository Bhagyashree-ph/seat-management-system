package com.iexceed.seatmanagement.audit.repository;

import com.iexceed.seatmanagement.audit.entity.AuditLog;
import com.iexceed.seatmanagement.audit.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository
        extends MongoRepository<AuditLog, String> {

    Page<AuditLog> findByEntityType(EntityType entityType, Pageable pageable);

    Page<AuditLog> findByUserEmail(String userEmail, Pageable pageable);

    Page<AuditLog> findByEntityTypeAndEntityId(EntityType entityType, String entityId, Pageable pageable);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(EntityType entityType, String entityId);
}