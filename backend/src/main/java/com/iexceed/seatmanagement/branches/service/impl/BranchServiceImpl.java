package com.iexceed.seatmanagement.branches.service.impl;

import com.iexceed.seatmanagement.branches.dto.request.CreateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.response.BranchResponse;
import com.iexceed.seatmanagement.branches.entity.Branch;
import com.iexceed.seatmanagement.branches.mapper.BranchMapper;
import com.iexceed.seatmanagement.branches.repository.BranchRepository;
import com.iexceed.seatmanagement.branches.service.BranchService;
import com.iexceed.seatmanagement.common.utils.AuditUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public BranchResponse createBranch(CreateBranchRequest request) {
        if(branchRepository.existsByBranchCodeAndDeletedFalse(request.getBranchCode())){
            throw new RuntimeException("Branch code already exists");
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
}
