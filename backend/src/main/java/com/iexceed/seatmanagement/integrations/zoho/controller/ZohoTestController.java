package com.iexceed.seatmanagement.integrations.zoho.controller;

import com.iexceed.seatmanagement.integrations.zoho.client.ZohoPeopleClient;
import com.iexceed.seatmanagement.integrations.zoho.dto.ZohoTokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/test/zoho")
@RequiredArgsConstructor
public class ZohoTestController {

    private final ZohoPeopleClient zohoAuthClient;

    @GetMapping("/token")
    public Mono<String> testZohoToken() {
        return zohoAuthClient.getAccessToken();
    }

    @GetMapping("/employees")
    public Mono<Map<String, Object>> getEmployees(@RequestParam String token, @RequestParam Integer from, @RequestParam Integer limit) {
        return zohoAuthClient.fetchEmployeesFromZoho(token, from, limit)
                .map(response -> Map.of(
                        "status", "success",
                        "data", response
                ))
                .onErrorResume(e -> Mono.just(Map.of(
                        "status", "error",
                        "message", e.getMessage()
                )));
    }
}