package com.iexceed.seatmanagement.floors.dto.response;

import com.iexceed.seatmanagement.floors.enums.FloorStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FloorResponse {

    private String id;

    private String floorCode;

    private String branchCode;

    private Integer floorNumber;

    private Integer totalSeats;

    private FloorStatus status;
}