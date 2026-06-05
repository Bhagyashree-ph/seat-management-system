package com.iexceed.seatmanagement.floors.repository;

import com.iexceed.seatmanagement.floors.entity.Floor;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorRepository
        extends MongoRepository<Floor, String> {
    List<Floor> findByDeletedFalse();
    boolean existsByFloorCodeAndDeletedFalse(String floorCode);
}