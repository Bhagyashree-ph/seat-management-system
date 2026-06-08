package com.iexceed.seatmanagement.branches.dto.request;

import com.iexceed.seatmanagement.branches.enums.BranchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateBranchRequest {
    @NotBlank
    private String branchName;

    @NotBlank
    private String city;

    private String address;

    @NotNull
    private BranchStatus status;

    @NotBlank
    private String timezone;
}
