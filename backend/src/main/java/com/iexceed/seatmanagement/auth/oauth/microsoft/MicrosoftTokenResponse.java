package com.iexceed.seatmanagement.auth.oauth.microsoft;

import lombok.Data;

@Data
public class MicrosoftTokenResponse {

    private String access_token;
    private String token_type;
    private Long expires_in;

}