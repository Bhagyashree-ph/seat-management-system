package com.iexceed.seatmanagement.integrations.zoho.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "zoho")
public class ZohoProperties {

    private String accountsUrl;
    private String peopleBaseUrl;
    private String clientId;
    private String clientSecret;
    private String refreshToken;
    private String tokenUrl;
}