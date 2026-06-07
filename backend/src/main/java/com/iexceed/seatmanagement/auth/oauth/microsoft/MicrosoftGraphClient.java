package com.iexceed.seatmanagement.auth.oauth.microsoft;

import com.iexceed.seatmanagement.auth.oauth.microsoft.config.MicrosoftOAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class MicrosoftGraphClient {

    private final MicrosoftOAuthProperties microsoftOAuthProperties;

    private final WebClient webClient = WebClient.builder().build();

    public MicrosoftTokenResponse exchangeCodeForToken(String code) {

        String tokenUrl = "https://login.microsoftonline.com/"
                + microsoftOAuthProperties.getTenantId()
                + "/oauth2/v2.0/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", microsoftOAuthProperties.getClientId());
        formData.add("client_secret", microsoftOAuthProperties.getClientSecret());
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", microsoftOAuthProperties.getRedirectUri());

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(MicrosoftTokenResponse.class)
                .block();
    }

    public MicrosoftUserProfile getUserProfile(String accessToken) {

        return webClient.get()
                .uri("https://graph.microsoft.com/v1.0/me")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(MicrosoftUserProfile.class)
                .block();
    }
}