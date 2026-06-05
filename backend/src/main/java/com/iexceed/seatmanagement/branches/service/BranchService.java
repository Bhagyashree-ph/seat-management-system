package com.iexceed.seatmanagement.branches.service;

import com.iexceed.seatmanagement.branches.dto.request.CreateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.response.BranchResponse;
import java.util.List;

public interface BranchService {

    BranchResponse createBranch(CreateBranchRequest request);

    List<BranchResponse> getAllBranches();
}