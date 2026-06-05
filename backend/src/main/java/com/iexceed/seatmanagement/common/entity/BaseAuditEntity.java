package com.iexceed.seatmanagement.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseAuditEntity {

    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

}