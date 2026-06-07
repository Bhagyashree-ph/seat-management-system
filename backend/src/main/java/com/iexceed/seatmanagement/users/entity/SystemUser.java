package com.iexceed.seatmanagement.users.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="system_users")
public class SystemUser {

    @Id
    private String id;
    @Indexed(unique = true)
    private String employeeId;
    @Indexed(unique = true)
    private String username;
    private String passwordHash;
    private Boolean enabled;
    @Builder.Default
    private Integer failedLoginAttempts = 0;
    private LocalDateTime lastLoginAt;
    private Boolean accountLocked;
    private LocalDateTime lockedAt;
}