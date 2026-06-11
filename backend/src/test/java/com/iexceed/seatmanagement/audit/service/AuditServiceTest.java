package com.iexceed.seatmanagement.audit.service;

import com.iexceed.seatmanagement.audit.dto.response.AuditLogResponse;
import com.iexceed.seatmanagement.audit.entity.AuditLog;
import com.iexceed.seatmanagement.audit.entity.FieldChange;
import com.iexceed.seatmanagement.audit.enums.AuditAction;
import com.iexceed.seatmanagement.audit.enums.EntityType;
import com.iexceed.seatmanagement.audit.repository.AuditLogRepository;
import com.iexceed.seatmanagement.audit.service.impl.AuditServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    private static final String ENTITY_ID = "branch-1";

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    private Pageable pageable;
    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        auditLog = AuditLog.builder()
                .id("audit-1")
                .entityType(EntityType.BRANCH)
                .entityId(ENTITY_ID)
                .action(AuditAction.UPDATE_BRANCH)
                .userEmail("admin@example.com")
                .changes(changes())
                .timestamp(LocalDateTime.of(2026, 6, 11, 10, 30))
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void log_success_savesAuditLogWithCurrentUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@example.com", "password")
        );
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        auditService.log(EntityType.BRANCH, ENTITY_ID, AuditAction.CREATE_BRANCH, changes());

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLog savedAuditLog = captor.getValue();
        assertEquals(EntityType.BRANCH, savedAuditLog.getEntityType());
        assertEquals(ENTITY_ID, savedAuditLog.getEntityId());
        assertEquals(AuditAction.CREATE_BRANCH, savedAuditLog.getAction());
        assertEquals("admin@example.com", savedAuditLog.getUserEmail());
        assertEquals("Old Branch", savedAuditLog.getChanges().get("branchName").getOldValue());
        assertNotNull(savedAuditLog.getTimestamp());
    }

    @Test
    void log_repositoryFailure_isSwallowed() {
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenThrow(new RuntimeException("database unavailable"));

        assertDoesNotThrow(() -> auditService.log(EntityType.BRANCH, ENTITY_ID, AuditAction.CREATE_BRANCH, changes()));

        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void getAuditLogs_filtersByEntityTypeAndEntityId() {
        when(auditLogRepository.findByEntityTypeAndEntityId(EntityType.BRANCH, ENTITY_ID, pageable))
                .thenReturn(new PageImpl<>(List.of(auditLog)));

        Page<AuditLogResponse> response = auditService.getAuditLogs(EntityType.BRANCH, ENTITY_ID, null, pageable);

        assertAuditLogResponse(response.getContent().get(0));
        verify(auditLogRepository).findByEntityTypeAndEntityId(EntityType.BRANCH, ENTITY_ID, pageable);
    }

    @Test
    void getAuditLogs_filtersByEntityType() {
        when(auditLogRepository.findByEntityType(EntityType.BRANCH, pageable))
                .thenReturn(new PageImpl<>(List.of(auditLog)));

        Page<AuditLogResponse> response = auditService.getAuditLogs(EntityType.BRANCH, null, null, pageable);

        assertAuditLogResponse(response.getContent().get(0));
        verify(auditLogRepository).findByEntityType(EntityType.BRANCH, pageable);
    }

    @Test
    void getAuditLogs_filtersByUserEmail() {
        when(auditLogRepository.findByUserEmail("admin@example.com", pageable))
                .thenReturn(new PageImpl<>(List.of(auditLog)));

        Page<AuditLogResponse> response = auditService.getAuditLogs(null, null, "admin@example.com", pageable);

        assertAuditLogResponse(response.getContent().get(0));
        verify(auditLogRepository).findByUserEmail("admin@example.com", pageable);
    }

    @Test
    void getAuditLogs_withoutFilters_returnsAll() {
        when(auditLogRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(auditLog)));

        Page<AuditLogResponse> response = auditService.getAuditLogs(null, null, null, pageable);

        assertAuditLogResponse(response.getContent().get(0));
        verify(auditLogRepository).findAll(pageable);
    }

    @Test
    void getEntityAuditHistory_success() {
        when(auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(EntityType.BRANCH, ENTITY_ID))
                .thenReturn(List.of(auditLog));

        List<AuditLogResponse> response = auditService.getEntityAuditHistory(EntityType.BRANCH, ENTITY_ID);

        assertEquals(1, response.size());
        assertAuditLogResponse(response.get(0));
        verify(auditLogRepository).findByEntityTypeAndEntityIdOrderByTimestampDesc(EntityType.BRANCH, ENTITY_ID);
    }

    private void assertAuditLogResponse(AuditLogResponse response) {
        assertEquals("audit-1", response.getId());
        assertEquals(EntityType.BRANCH, response.getEntityType());
        assertEquals(ENTITY_ID, response.getEntityId());
        assertEquals(AuditAction.UPDATE_BRANCH, response.getAction());
        assertEquals("admin@example.com", response.getUserEmail());
        assertEquals("Old Branch", response.getChanges().get("branchName").getOldValue());
        assertEquals("New Branch", response.getChanges().get("branchName").getNewValue());
        assertEquals(LocalDateTime.of(2026, 6, 11, 10, 30), response.getTimestamp());
    }

    private Map<String, FieldChange> changes() {
        return Map.of(
                "branchName",
                FieldChange.builder()
                        .oldValue("Old Branch")
                        .newValue("New Branch")
                        .build()
        );
    }
}
