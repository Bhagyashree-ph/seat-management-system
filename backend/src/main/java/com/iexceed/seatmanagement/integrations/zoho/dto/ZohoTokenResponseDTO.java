package com.iexceed.seatmanagement.integrations.zoho.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZohoTokenResponseDTO {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("api_domain")
    private String apiDomain;

    @JsonProperty("token_type")
    private String tokenType;
}