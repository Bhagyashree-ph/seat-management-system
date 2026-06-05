package com.iexceed.seatmanagement.floors.service;

import com.iexceed.seatmanagement.floors.dto.request.CreateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.response.FloorResponse;

import java.util.List;

public interface FloorService {

    FloorResponse createFloor( CreateFloorRequest request);

    List<FloorResponse> getAllFloors();
}