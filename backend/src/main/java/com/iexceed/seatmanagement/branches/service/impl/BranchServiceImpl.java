package com.iexceed.seatmanagement.branches.service.impl;

import com.iexceed.seatmanagement.audit.entity.FieldChange;
import com.iexceed.seatmanagement.audit.enums.AuditAction;
import com.iexceed.seatmanagement.audit.enums.EntityType;
import com.iexceed.seatmanagement.audit.service.AuditService;
import com.iexceed.seatmanagement.audit.util.AuditDiffUtil;
import com.iexceed.seatmanagement.audit.util.AuditObjectCloneUtil;
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
import com.iexceed.seatmanagement.security.util.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final AuditService auditService;

    @Override
    public BranchResponse createBranch(CreateBranchRequest request) {
        if (branchRepository.existsByBranchCodeAndDeletedFalse(request.getBranchCode())) {
            throw new BranchAlreadyExistsException("Branch code already exists");
        }
        if (branchRepository.existsByBranchNameAndDeletedFalse(request.getBranchName())) {
            throw new BranchAlreadyExistsException("Branch name already exists");
        }
        Branch branch = BranchMapper.toEntity(request);
        AuditUtil.setCreateAudit(branch);
        Branch savedBranch = branchRepository.save(branch);
        auditService.log(EntityType.BRANCH, savedBranch.getId(), AuditAction.CREATE_BRANCH, Map.of());
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
        Branch beforeDeactivation = AuditObjectCloneUtil.deepCopy(branch, Branch.class);
        branch.setStatus(BranchStatus.INACTIVE);
        AuditUtil.setDeleteAudit(branch);
        Branch deactivatedBranch = branchRepository.save(branch);
        Map<String, FieldChange> changes = AuditDiffUtil.compare(beforeDeactivation, deactivatedBranch);
        auditService.log(EntityType.BRANCH, deactivatedBranch.getId(), AuditAction.DELETE_BRANCH, changes);
    }

    @Override
    public BranchResponse updateBranch(String branchId, UpdateBranchRequest updateBranchRequest) {
        Branch branch = branchRepository.findByIdAndDeletedFalse(branchId)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with id: " + branchId));
        if (!branch.getBranchName().equalsIgnoreCase(updateBranchRequest.getBranchName()) && branchRepository.existsByBranchNameAndDeletedFalse(updateBranchRequest.getBranchName())) {
            throw new BranchAlreadyExistsException("Branch already exists");
        }
        Branch beforeUpdate = AuditObjectCloneUtil.deepCopy(branch, Branch.class);
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
        AuditUtil.setUpdateAudit(branch);
        Branch savedBranch = branchRepository.save(branch);
        Map<String, FieldChange> changes = AuditDiffUtil.compare(beforeUpdate, savedBranch);
        auditService.log(EntityType.BRANCH, savedBranch.getId(), AuditAction.UPDATE_BRANCH, changes);
        return BranchMapper.toResponse(savedBranch);
    }
}
