package com.iexceed.seatmanagement.audit.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AuditObjectCloneUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private AuditObjectCloneUtil() {
    }

    public static <T> T deepCopy(T source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        try {
            String json = OBJECT_MAPPER.writeValueAsString(source);
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception ex) {
            log.error("Failed to clone object for audit",ex);
            throw new RuntimeException("Failed to clone object for audit", ex);
        }
    }
}