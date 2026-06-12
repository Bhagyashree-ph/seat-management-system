package com.iexceed.seatmanagement.floors.dto.response;

import com.iexceed.seatmanagement.floors.enums.FloorStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FloorResponse {

    private String id;
    private String branchId;
    private String floorCode;
    private String floorName;
    private Integer totalSeats;
    private boolean layoutEnabled;
    private String activeLayoutVersionId;
    private FloorStatus status;
}