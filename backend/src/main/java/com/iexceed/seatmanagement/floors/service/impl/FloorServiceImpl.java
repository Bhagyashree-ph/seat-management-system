package com.iexceed.seatmanagement.floors.service.impl;

import com.iexceed.seatmanagement.common.utils.AuditUtil;
import com.iexceed.seatmanagement.floors.dto.request.CreateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.response.FloorResponse;
import com.iexceed.seatmanagement.floors.entity.Floor;
import com.iexceed.seatmanagement.floors.mapper.FloorMapper;
import com.iexceed.seatmanagement.floors.repository.FloorRepository;
import com.iexceed.seatmanagement.floors.service.FloorService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FloorServiceImpl
        implements FloorService {

    private final FloorRepository floorRepository;

    @Override
    public FloorResponse createFloor(CreateFloorRequest request) {
        if(floorRepository.existsByFloorCodeAndDeletedFalse(request.getFloorCode())){
            throw new RuntimeException("Floor code already exists");
        }
        Floor floor = FloorMapper.toEntity(request);
        AuditUtil.setCreateAudit(floor, "SYSTEM");
        Floor savedFloor = floorRepository.save(floor);
        return FloorMapper.toResponse(savedFloor);
    }

    @Override
    public List<FloorResponse> getAllFloors() {
        return floorRepository.findByDeletedFalse()
                .stream()
                .map(FloorMapper::toResponse)
                .toList();
    }
}