package com.iexceed.seatmanagement.employees.mapper;

import com.iexceed.seatmanagement.employees.dto.zoho.ZohoEmployeeRecordDTO;
import com.iexceed.seatmanagement.employees.entity.Employee;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    public static Employee toEmployee(ZohoEmployeeRecordDTO dto) {
        return Employee.builder()
                .employeeCode(dto.getEmployeeId())
                .zohoEmployeeId(dto.getZohoId())
                .zuid(dto.getZuid())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .displayName(dto.getFullName())
                .nickName(dto.getNickName())
                .email(dto.getEmail())
                .mobile(dto.getMobile())
                .workPhone(dto.getWorkPhone())
                .extension(dto.getExtension())
                .designation(dto.getDesignation())
                .designationId(dto.getDesignationId())
                .department(dto.getDepartment())
                .departmentId(dto.getDepartmentId())
                .grade(dto.getGrade())
                .gradeId(dto.getGradeId())
                .workLocation(dto.getWorkLocation())
                .officeLocation(dto.getOfficeLocation())
                .seatingLocation(dto.getSeatingLocation())
                .seatingLocationId(dto.getSeatingLocationId())
                .reportingManagerName(dto.getReportingManagerName())
                .reportingManagerZohoId(dto.getReportingManagerZohoId())
                .reportingManagerEmail(dto.getReportingManagerEmail())
                .secondReportingManagerName(dto.getSecondReportingManagerName())
                .secondReportingManagerZohoId(dto.getSecondReportingManagerZohoId())
                .employmentStatus(dto.getEmploymentStatus())
                .employmentStatusType(dto.getEmploymentStatusType())
                .primarySkills(parseSkills(dto.getSkills()))
                .secondarySkills(parseSkills(dto.getSecondarySkills()))
                .expertise(dto.getExpertise())
                .aboutMe(dto.getAboutMe())
                .profilePhotoUrl(dto.getProfilePhotoUrl())
                .active(isActiveEmployee(dto.getEmploymentStatus()))
                .deleted(false)
                .source("ZOHO")
                .zohoCreatedTime(parseInstant(dto.getCreatedTime()))
                .zohoModifiedTime(parseInstant(dto.getModifiedTime()))
                .lastSyncedAt(Instant.now())
                .build();
    }

    private static List<String> parseSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .collect(Collectors.toList());
    }

    private static Boolean isActiveEmployee(String status) {
        if (status == null) {
            return false;
        }
        return "active".equalsIgnoreCase(status);
    }

    private static Instant parseInstant(String epochMillis) {
        try {
            if (epochMillis == null || epochMillis.isBlank()) {
                return null;
            }
            return Instant.ofEpochMilli(Long.parseLong(epochMillis));
        } catch (Exception ex) {
            return null;
        }
    }
}