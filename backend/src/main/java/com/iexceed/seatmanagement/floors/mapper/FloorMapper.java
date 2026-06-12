package com.iexceed.seatmanagement.floors.mapper;

import com.iexceed.seatmanagement.floors.dto.request.CreateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.request.UpdateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.response.FloorResponse;
import com.iexceed.seatmanagement.floors.entity.Floor;

public class FloorMapper {

    private FloorMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Floor toEntity(CreateFloorRequest request) {
        return Floor.builder()
                .branchId(request.getBranchId())
                .floorCode(request.getFloorCode())
                .floorName(request.getFloorName())
                .totalSeats(request.getTotalSeats())
                .status(request.getStatus())
                .layoutEnabled(false)
                .activeLayoutVersionId(null)
                .build();
    }

    public static FloorResponse toResponse(Floor floor) {
        return FloorResponse.builder()
                .id(floor.getId())
                .branchId(floor.getBranchId())
                .floorCode(floor.getFloorCode())
                .floorName(floor.getFloorName())
                .totalSeats(floor.getTotalSeats())
                .layoutEnabled(floor.isLayoutEnabled())
                .activeLayoutVersionId(floor.getActiveLayoutVersionId())
                .status(floor.getStatus())
                .build();
    }

    public static void updateEntity(Floor floor, UpdateFloorRequest request) {
        floor.setFloorCode(request.getFloorCode());
        floor.setFloorName(request.getFloorName());
        floor.setTotalSeats(request.getTotalSeats());
        floor.setStatus(request.getStatus());
    }
}