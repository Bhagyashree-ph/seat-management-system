package com.iexceed.seatmanagement.branches.mapper;

import com.iexceed.seatmanagement.branches.dto.request.CreateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.response.BranchResponse;
import com.iexceed.seatmanagement.branches.entity.Branch;

public class BranchMapper {

    private BranchMapper() {
    }

    public static Branch toEntity(CreateBranchRequest request) {
        return Branch.builder()
                .branchCode(request.getBranchCode())
                .branchName(request.getBranchName())
                .city(request.getCity())
                .address(request.getAddress())
                .status(request.getStatus())
                .timezone(request.getTimezone())
                .build();
    }

    public static BranchResponse toResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .branchCode(branch.getBranchCode())
                .branchName(branch.getBranchName())
                .city(branch.getCity())
                .address(branch.getAddress())
                .status(branch.getStatus())
                .deleted(branch.isDeleted())
                .build();
    }
}