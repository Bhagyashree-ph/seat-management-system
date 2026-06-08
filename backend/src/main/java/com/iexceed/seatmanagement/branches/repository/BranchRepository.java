package com.iexceed.seatmanagement.branches.repository;

import com.iexceed.seatmanagement.branches.entity.Branch;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BranchRepository
        extends MongoRepository<Branch, String> {
    List<Branch> findByDeletedFalse();
    boolean existsByBranchCodeAndDeletedFalse(String branchCode);
    Optional<Branch> findByIdAndDeletedFalse(String id);
    boolean existsByBranchNameAndDeletedFalse(String branchName);
}