package com.iexceed.seatmanagement.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrentUserResponse {

    private String employeeCode;
    private String email;
    private String displayName;
}