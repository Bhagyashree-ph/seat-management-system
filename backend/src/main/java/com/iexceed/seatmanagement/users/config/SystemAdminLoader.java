package com.iexceed.seatmanagement.users.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemAdminLoader {

    private final ObjectMapper objectMapper;

    public List<SystemAdminConfig> loadSystemAdmins() throws Exception {
        log.info("Loading system admin configurations from JSON file");
        List<SystemAdminConfig> admins = objectMapper.readValue(
                new ClassPathResource("static/system-admins.json").getInputStream(),
                new TypeReference<List<SystemAdminConfig>>() {}
        );
        admins.forEach(admin -> admin.setPassword(
                resolvePassword(admin.getPassword())
        ));
        return admins;
    }

    private String resolvePassword(String password) {
        if (password == null || !password.startsWith("$") || !password.endsWith("}")) {
            return password;
        }
        String envKey = password.substring(2, password.length() - 1);
        String envValue = System.getenv(envKey);
        if (envValue == null) {
            log.warn("Environment variable {} not found for password resolution", envKey);
            throw new IllegalStateException("Missing environment variable: " + envKey);
        }
        return envValue;
    }
}
