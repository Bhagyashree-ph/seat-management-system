package com.iexceed.seatmanagement.common.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
public abstract class BaseAuditEntity {

    @Indexed
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

}