package com.iexceed.seatmanagement.floors.dto.request;

import com.iexceed.seatmanagement.floors.enums.FloorStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateFloorRequest {

    @NotBlank(message = "Floor code is required")
    private String floorCode;

    @NotBlank(message = "Floor name is required")
    private String floorName;

    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be greater than 0")
    private Integer totalSeats;

    @NotNull(message = "Floor status is required")
    private FloorStatus status;
}