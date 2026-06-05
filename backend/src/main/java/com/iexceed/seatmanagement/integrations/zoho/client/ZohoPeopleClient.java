package com.iexceed.seatmanagement.integrations.zoho.client;

import com.iexceed.seatmanagement.integrations.zoho.config.ZohoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class ZohoPeopleClient {

    private static final String EMPLOYEE_API =
            "/people/api/forms/employee/getRecords";

    private static final long TOKEN_EXPIRY_BUFFER_SECONDS = 60;

    private final WebClient zohoWebClient;
    private final ZohoProperties zohoProperties;

    private final AtomicReference<String> cachedToken =
            new AtomicReference<>();

    private final AtomicReference<Instant> tokenExpiry =
            new AtomicReference<>();

    /**
     * Generates and caches Zoho OAuth access token.
     */
    public Mono<String> getAccessToken() {

        if (isTokenValid()) {
            return Mono.just(cachedToken.get());
        }

        log.info("Generating new Zoho access token");

        MultiValueMap<String, String> formData =
                new LinkedMultiValueMap<>();

        formData.add("grant_type", "refresh_token");
        formData.add("client_id", zohoProperties.getClientId());
        formData.add("client_secret", zohoProperties.getClientSecret());
        formData.add("refresh_token", zohoProperties.getRefreshToken());

        return zohoWebClient.post()
                .uri(zohoProperties.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(this::cacheAndReturnToken)
                .doOnError(error ->
                        log.error("Failed to generate Zoho access token", error)
                );
    }

    /**
     * Fetch employees from Zoho People.
     */
    public Mono<Map<String, Object>> fetchEmployeesFromZoho(
            String token,
            int from,
            int limit
    ) {

        log.info("Fetching employees from Zoho. from={}, limit={}",
                from, limit);

        return zohoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(EMPLOYEE_API)
                        .queryParam("sIndex", from)
                        .queryParam("limit", limit)
                        .build()
                )
                .header("Authorization", "Zoho-oauthtoken " + token)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Zoho Employee API Error: {}",
                                            errorBody);

                                    return Mono.error(
                                            new RuntimeException(
                                                    "Zoho Employee API Error: "
                                                            + errorBody
                                            )
                                    );
                                })
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(15))
                .doOnError(error ->
                        log.error("Failed to fetch employees from Zoho", error)
                );
    }

    /**
     * Validate cached token with safety expiry buffer.
     */
    private boolean isTokenValid() {

        return cachedToken.get() != null
                && tokenExpiry.get() != null
                && Instant.now()
                .plusSeconds(TOKEN_EXPIRY_BUFFER_SECONDS)
                .isBefore(tokenExpiry.get());
    }

    /**
     * Cache token and return token value.
     */
    private String cacheAndReturnToken(Map<String, Object> response) {

        String token = (String) response.get("access_token");

        Integer expiresIn = (Integer) response.get("expires_in");

        long expirySeconds = expiresIn != null
                ? expiresIn
                : 3600;

        cachedToken.set(token);

        tokenExpiry.set(
                Instant.now().plusSeconds(expirySeconds)
        );

        log.info("Zoho access token generated successfully");

        return token;
    }
}