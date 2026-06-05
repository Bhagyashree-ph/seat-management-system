package com.iexceed.seatmanagement.integrations.zoho.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class ZohoWebClientConfig {

    private final ZohoProperties zohoProperties;

    @Bean
    public WebClient zohoWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(zohoProperties.getPeopleBaseUrl())
                .build();
    }
}