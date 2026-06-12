package com.iexceed.seatmanagement.floors.controller;

import com.iexceed.seatmanagement.common.response.ApiResponse;
import com.iexceed.seatmanagement.floors.dto.request.CreateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.request.UpdateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.response.FloorResponse;
import com.iexceed.seatmanagement.floors.service.FloorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/floors")
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ApiResponse<FloorResponse> createFloor(@Valid @RequestBody CreateFloorRequest request) {
        return ApiResponse.<FloorResponse>builder()
                .success(true)
                .message("Floor created successfully")
                .data(floorService.createFloor(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<FloorResponse>> getAllFloors() {
        return ApiResponse.<List<FloorResponse>>builder()
                .success(true)
                .message("Floors fetched successfully")
                .data(floorService.getAllFloors())
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<FloorResponse> getFloorById(
            @PathVariable String id) {

        return ApiResponse.<FloorResponse>builder()
                .success(true)
                .message("Floor fetched successfully")
                .data(floorService.getFloorById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ApiResponse<FloorResponse> updateFloor(
            @PathVariable String id,
            @Valid @RequestBody UpdateFloorRequest request) {

        return ApiResponse.<FloorResponse>builder()
                .success(true)
                .message("Floor updated successfully")
                .data(floorService.updateFloor(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN')")
    public ApiResponse<Void> deleteFloor(@PathVariable String id) {

        floorService.deleteFloor(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Floor deleted successfully")
                .build();
    }
}