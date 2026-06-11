package com.iexceed.seatmanagement.audit.util;

import com.iexceed.seatmanagement.audit.entity.FieldChange;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class AuditDiffUtil {

    private static final Set<String> IGNORED_FIELDS = Set.of(
            "createdAt",
            "updatedAt",
            "createdBy",
            "updatedBy"
    );

    private AuditDiffUtil() {
    }

    public static Map<String, FieldChange> compare(
            Object oldObject,
            Object newObject) {

        Map<String, FieldChange> changes = new HashMap<>();

        if (oldObject == null || newObject == null) {
            return changes;
        }

        Class<?> clazz = oldObject.getClass();

        while (clazz != null && clazz != Object.class) {

            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {

                if (IGNORED_FIELDS.contains(field.getName())) {
                    continue;
                }

                field.setAccessible(true);

                try {

                    Object oldValue = field.get(oldObject);
                    Object newValue = field.get(newObject);

                    if (!Objects.equals(oldValue, newValue)) {

                        changes.put(
                                field.getName(),
                                FieldChange.builder()
                                        .oldValue(oldValue)
                                        .newValue(newValue)
                                        .build()
                        );
                    }

                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(
                            "Failed to compare field: " + field.getName(),
                            ex
                    );
                }
            }

            clazz = clazz.getSuperclass();
        }

        return changes;
    }
}