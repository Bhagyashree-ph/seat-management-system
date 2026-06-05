package com.iexceed.seatmanagement.floors.mapper;

import com.iexceed.seatmanagement.floors.dto.request.CreateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.response.FloorResponse;
import com.iexceed.seatmanagement.floors.entity.Floor;

public class FloorMapper {

    private FloorMapper() {
    }

    public static Floor toEntity(
            CreateFloorRequest request
    ) {

        return Floor.builder()
                .floorCode(request.getFloorCode())
                .branchCode(request.getBranchCode())
                .floorNumber(request.getFloorNumber())
                .totalSeats(request.getTotalSeats())
                .status(request.getStatus())
                .build();
    }

    public static FloorResponse toResponse(
            Floor floor
    ) {

        return FloorResponse.builder()
                .id(floor.getId())
                .floorCode(floor.getFloorCode())
                .branchCode(floor.getBranchCode())
                .floorNumber(floor.getFloorNumber())
                .totalSeats(floor.getTotalSeats())
                .status(floor.getStatus())
                .build();
    }
}