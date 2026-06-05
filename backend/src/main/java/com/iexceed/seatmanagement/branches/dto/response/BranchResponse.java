package com.iexceed.seatmanagement.branches.dto.response;

import com.iexceed.seatmanagement.branches.enums.BranchStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchResponse {

    private String id;

    private String branchCode;

    private String branchName;

    private String city;

    private String address;

    private BranchStatus status;

    private boolean deleted;
}