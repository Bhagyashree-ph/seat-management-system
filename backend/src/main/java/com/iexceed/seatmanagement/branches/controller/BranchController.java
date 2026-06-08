package com.iexceed.seatmanagement.branches.controller;

import com.iexceed.seatmanagement.branches.dto.request.CreateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.request.UpdateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.response.BranchResponse;
import com.iexceed.seatmanagement.branches.service.BranchService;
import com.iexceed.seatmanagement.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ApiResponse<BranchResponse> createBranch(@Valid @RequestBody CreateBranchRequest request) {
        return ApiResponse.<BranchResponse>builder()
                .success(true)
                .message("Branch created successfully")
                .data(branchService.createBranch(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<BranchResponse>> getAllBranches() {
        return ApiResponse.<List<BranchResponse>>builder()
                .success(true)
                .message("Branches fetched successfully")
                .data(branchService.getAllBranches())
                .build();
    }

    @GetMapping("/{branchId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<BranchResponse> getBranchById(@PathVariable String branchId) {
        return ApiResponse.<BranchResponse>builder()
                .success(true)
                .message("Branch fetched successfully")
                .data(branchService.getBranchById(branchId))
                .build();
    }

    @DeleteMapping("/{branchId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Void> deactivateBranch(@PathVariable String branchId) {
        branchService.deactivateBranch(branchId);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Branch deactivated successfully")
                .data(null)
                .build();
    }

    @PutMapping("/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ApiResponse<BranchResponse> updateBranch(@PathVariable String branchId, @Valid @RequestBody UpdateBranchRequest updateBranchRequest) {
        return ApiResponse.<BranchResponse>builder()
                .success(true)
                .message("Branch updated successfully")
                .data(branchService.updateBranch(branchId, updateBranchRequest))
                .build();
    }
}