package com.iexceed.seatmanagement.branches.service.impl;

import com.iexceed.seatmanagement.branches.dto.request.CreateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.request.UpdateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.response.BranchResponse;
import com.iexceed.seatmanagement.branches.entity.Branch;
import com.iexceed.seatmanagement.branches.enums.BranchStatus;
import com.iexceed.seatmanagement.branches.exceptions.BranchAlreadyExistsException;
import com.iexceed.seatmanagement.branches.exceptions.BranchNotFoundException;
import com.iexceed.seatmanagement.branches.mapper.BranchMapper;
import com.iexceed.seatmanagement.branches.repository.BranchRepository;
import com.iexceed.seatmanagement.branches.service.BranchService;
import com.iexceed.seatmanagement.common.utils.AuditUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public BranchResponse createBranch(CreateBranchRequest request) {
        if (branchRepository.existsByBranchCodeAndDeletedFalse(request.getBranchCode())) {
            throw new BranchAlreadyExistsException("Branch code already exists");
        }
        if (branchRepository.existsByBranchNameAndDeletedFalse(request.getBranchName())) {
            throw new BranchAlreadyExistsException("Branch name already exists");
        }
        Branch branch = BranchMapper.toEntity(request);
        AuditUtil.setCreateAudit(branch, "SYSTEM");
        Branch savedBranch = branchRepository.save(branch);
        return BranchMapper.toResponse(savedBranch);
    }

    @Override
    public List<BranchResponse> getAllBranches() {
        return branchRepository.findByDeletedFalse()
                .stream().map(BranchMapper::toResponse)
                .toList();
    }

    @Override
    public BranchResponse getBranchById(String branchId) {
        return branchRepository.findByIdAndDeletedFalse(branchId)
                .map(BranchMapper::toResponse)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with id: " + branchId));
    }

    @Override
    public void deactivateBranch(String branchId) {
        Branch branch = branchRepository.findByIdAndDeletedFalse(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with id: " + branchId));
        branch.setStatus(BranchStatus.INACTIVE);
        branch.setDeleted(true);
        branch.setUpdatedAt(LocalDateTime.now());
        branch.setUpdatedBy("SYSTEM");
        branchRepository.save(branch);
    }

    @Override
    public BranchResponse updateBranch(String branchId, UpdateBranchRequest updateBranchRequest) {
        Branch branch = branchRepository.findByIdAndDeletedFalse(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with id: " + branchId));
        if (!branch.getBranchName().equalsIgnoreCase(updateBranchRequest.getBranchName()) && branchRepository.existsByBranchNameAndDeletedFalse(updateBranchRequest.getBranchName())) {
            throw new BranchAlreadyExistsException("Branch already exists");
        }
        try {
            ZoneId.of(updateBranchRequest.getTimezone());
        } catch (Exception e) {
            throw new RuntimeException("Invalid timezone");
        }
        branch.setBranchName(updateBranchRequest.getBranchName());
        branch.setCity(updateBranchRequest.getCity());
        branch.setAddress(updateBranchRequest.getAddress());
        branch.setTimezone(updateBranchRequest.getTimezone());
        branch.setStatus(updateBranchRequest.getStatus());

        AuditUtil.setUpdateAudit(branch, "SYSTEM");

        return BranchMapper.toResponse(branchRepository.save(branch));
    }
}
