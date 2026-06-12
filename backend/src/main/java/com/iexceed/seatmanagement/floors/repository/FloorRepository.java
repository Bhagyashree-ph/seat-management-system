package com.iexceed.seatmanagement.floors.repository;

import com.iexceed.seatmanagement.floors.entity.Floor;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FloorRepository extends MongoRepository<Floor, String> {

    Optional<Floor> findByIdAndDeletedFalse(String id);

    List<Floor> findByDeletedFalse();

    List<Floor> findByBranchIdAndDeletedFalse(String branchId);

    boolean existsByBranchIdAndFloorCodeAndDeletedFalse(
            String branchId,
            String floorCode
    );

    boolean existsByBranchIdAndFloorCodeAndDeletedFalseAndIdNot(
            String branchId,
            String floorCode,
            String id
    );
}