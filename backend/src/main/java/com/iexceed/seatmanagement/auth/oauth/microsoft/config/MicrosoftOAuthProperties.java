package com.iexceed.seatmanagement.auth.oauth.microsoft.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "microsoft")
public class MicrosoftOAuthProperties {

    private String tenantId;
    private String clientId;
    private String clientSecret;
    private String redirectUri;

}