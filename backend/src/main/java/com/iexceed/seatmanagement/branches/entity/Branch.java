package com.iexceed.seatmanagement.branches.entity;

import com.iexceed.seatmanagement.branches.enums.BranchStatus;
import com.iexceed.seatmanagement.common.entity.BaseAuditEntity;
import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "branches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch extends BaseAuditEntity {

    @Id
    private String id;
    @Indexed(unique = true)
    private String branchCode;
    private String branchName;
    private String city;
    private String address;
    private BranchStatus status;
    private String timezone;

}