package com.iexceed.seatmanagement.branches.dto.request;

import com.iexceed.seatmanagement.branches.enums.BranchStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateBranchRequest {

    @NotBlank
    private String branchCode;

    @NotBlank
    private String branchName;

    @NotBlank
    private String city;

    @NotBlank
    private String address;

    @NotNull
    private BranchStatus status;

    private String timezone;
}