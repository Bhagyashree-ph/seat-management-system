package com.iexceed.seatmanagement.floors.controller;

import com.iexceed.seatmanagement.branches.dto.response.BranchResponse;
import com.iexceed.seatmanagement.common.response.ApiResponse;
import com.iexceed.seatmanagement.floors.dto.request.CreateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.response.FloorResponse;
import com.iexceed.seatmanagement.floors.service.FloorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/floors")
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;

    @PostMapping
    public ApiResponse<FloorResponse> createFloor(@Valid @RequestBody CreateFloorRequest request) {
        return ApiResponse.<FloorResponse>builder()
                .success(true)
                .message("Branch created successfully")
                .data(floorService.createFloor(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<FloorResponse>> getAllFloors() {
        return ApiResponse.<List<FloorResponse>>builder()
                .success(true)
                .message("Branches fetched successfully")
                .data(floorService.getAllFloors())
                .build();
    }
}