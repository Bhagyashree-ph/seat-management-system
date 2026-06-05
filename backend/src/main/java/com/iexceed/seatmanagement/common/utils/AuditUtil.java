package com.iexceed.seatmanagement.common.utils;

import com.iexceed.seatmanagement.common.entity.BaseAuditEntity;

import java.time.LocalDateTime;

public class AuditUtil {

    private AuditUtil() {
    }

    public static void setCreateAudit(BaseAuditEntity entity, String user) {
        entity.setDeleted(false);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setCreatedBy(user);
        entity.setUpdatedBy(user);
    }

    public static void setUpdateAudit(BaseAuditEntity entity, String user) {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(user);
    }
}