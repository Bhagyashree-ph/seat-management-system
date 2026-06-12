package com.iexceed.seatmanagement.floors.service;

import com.iexceed.seatmanagement.floors.dto.request.CreateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.request.UpdateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.response.FloorResponse;

import java.util.List;

public interface FloorService {

    FloorResponse createFloor(CreateFloorRequest request);

    FloorResponse updateFloor(String floorId, UpdateFloorRequest request);

    FloorResponse getFloorById(String floorId);

    List<FloorResponse> getAllFloors();

    void deleteFloor(String floorId);
}