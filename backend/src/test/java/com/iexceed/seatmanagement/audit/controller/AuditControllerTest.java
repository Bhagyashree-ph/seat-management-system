package com.iexceed.seatmanagement.audit.controller;

import com.iexceed.seatmanagement.audit.dto.response.AuditLogResponse;
import com.iexceed.seatmanagement.audit.entity.FieldChange;
import com.iexceed.seatmanagement.audit.enums.AuditAction;
import com.iexceed.seatmanagement.audit.enums.EntityType;
import com.iexceed.seatmanagement.audit.service.AuditService;
import com.iexceed.seatmanagement.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuditControllerTest {

    private static final String ENTITY_ID = "branch-1";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditService auditService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void getAuditLogs_success() throws Exception {
        AuditLogResponse response = auditLogResponse();
        when(auditService.getAuditLogs(eq(EntityType.BRANCH), eq(ENTITY_ID), eq("admin@example.com"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/v1/audit")
                        .param("entityType", "BRANCH")
                        .param("entityId", ENTITY_ID)
                        .param("userEmail", "admin@example.com")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Audit logs retrieved successfully"))
                .andExpect(jsonPath("$.data.content[0].id").value("audit-1"))
                .andExpect(jsonPath("$.data.content[0].entityType").value("BRANCH"))
                .andExpect(jsonPath("$.data.content[0].entityId").value(ENTITY_ID))
                .andExpect(jsonPath("$.data.content[0].action").value("UPDATE_BRANCH"))
                .andExpect(jsonPath("$.data.content[0].userEmail").value("admin@example.com"))
                .andExpect(jsonPath("$.data.content[0].changes.branchName.oldValue").value("Old Branch"))
                .andExpect(jsonPath("$.data.content[0].changes.branchName.newValue").value("New Branch"));

        verify(auditService).getAuditLogs(eq(EntityType.BRANCH), eq(ENTITY_ID), eq("admin@example.com"), any(Pageable.class));
    }

    @Test
    void getEntityAuditHistory_success() throws Exception {
        when(auditService.getEntityAuditHistory(EntityType.BRANCH, ENTITY_ID))
                .thenReturn(List.of(auditLogResponse()));

        mockMvc.perform(get("/v1/audit/entity/{entityType}/{entityId}", EntityType.BRANCH, ENTITY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Audit history for entity retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value("audit-1"))
                .andExpect(jsonPath("$.data[0].entityType").value("BRANCH"))
                .andExpect(jsonPath("$.data[0].entityId").value(ENTITY_ID));

        verify(auditService).getEntityAuditHistory(EntityType.BRANCH, ENTITY_ID);
    }

    private AuditLogResponse auditLogResponse() {
        return AuditLogResponse.builder()
                .id("audit-1")
                .entityType(EntityType.BRANCH)
                .entityId(ENTITY_ID)
                .action(AuditAction.UPDATE_BRANCH)
                .userEmail("admin@example.com")
                .changes(Map.of(
                        "branchName",
                        FieldChange.builder()
                                .oldValue("Old Branch")
                                .newValue("New Branch")
                                .build()
                ))
                .timestamp(LocalDateTime.of(2026, 6, 11, 10, 30))
                .build();
    }
}
