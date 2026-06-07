package com.iexceed.seatmanagement.auth.oauth.microsoft;

import lombok.Data;

@Data
public class MicrosoftUserProfile {

    private String id;
    private String displayName;
    private String mail;
    private String userPrincipalName;

}