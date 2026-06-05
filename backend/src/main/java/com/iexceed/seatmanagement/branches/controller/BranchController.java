package com.iexceed.seatmanagement.branches.controller;

import com.iexceed.seatmanagement.branches.dto.request.CreateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.response.BranchResponse;
import com.iexceed.seatmanagement.branches.service.BranchService;
import com.iexceed.seatmanagement.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    public ApiResponse<BranchResponse> createBranch(@Valid @RequestBody CreateBranchRequest request) {
        return ApiResponse.<BranchResponse>builder()
                .success(true)
                .message("Branch created successfully")
                .data(branchService.createBranch(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<BranchResponse>> getAllBranches() {
        return ApiResponse.<List<BranchResponse>>builder()
                .success(true)
                .message("Branches fetched successfully")
                .data(branchService.getAllBranches())
                .build();
    }
}