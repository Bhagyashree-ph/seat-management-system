package com.iexceed.seatmanagement.common.utils;

import com.iexceed.seatmanagement.common.entity.BaseAuditEntity;
import com.iexceed.seatmanagement.security.util.CurrentUserUtil;

import java.time.LocalDateTime;

public final class AuditUtil {

    private AuditUtil() {
    }

    public static void setCreateAudit(BaseAuditEntity entity) {
        String userEmail = CurrentUserUtil.getCurrentUserEmail();
        LocalDateTime now = LocalDateTime.now();
        entity.setDeleted(false);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setCreatedBy(userEmail);
        entity.setUpdatedBy(userEmail);
    }

    public static void setUpdateAudit(BaseAuditEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(CurrentUserUtil.getCurrentUserEmail());
    }

    public static void setDeleteAudit(BaseAuditEntity entity) {
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(CurrentUserUtil.getCurrentUserEmail());
    }
}