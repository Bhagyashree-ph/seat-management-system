package com.iexceed.seatmanagement.roles.entity;

import com.iexceed.seatmanagement.roles.enums.RoleCode;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employee_roles")
@CompoundIndex(
        name = "employee_role_unique",
        def = "{'employeeId':1,'roleCode':1}",
        unique = true
)
public class EmployeeRole {

    @Id
    private String id;

    private String employeeId;

    private RoleCode roleCode;

    private Boolean active;

    private Instant assignedAt;

    private String assignedBy;
}
