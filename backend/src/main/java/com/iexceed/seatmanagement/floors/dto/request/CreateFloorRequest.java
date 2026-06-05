package com.iexceed.seatmanagement.floors.dto.request;

import com.iexceed.seatmanagement.floors.enums.FloorStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateFloorRequest {

    @NotBlank
    private String floorCode;

    @NotBlank
    private String branchCode;

    @NotNull
    private Integer floorNumber;

    @NotNull
    private Integer totalSeats;

    @NotNull
    private FloorStatus status;
}