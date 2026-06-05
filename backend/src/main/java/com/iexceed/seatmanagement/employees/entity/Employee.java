package com.iexceed.seatmanagement.employees.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employees")
public class Employee {

    @Id
    private String id;

    @Indexed(unique = true)
    private String employeeCode;

    @Indexed
    private String zohoEmployeeId;

    private String zuid;

    private String salutation;

    @Indexed
    private String firstName;

    @Indexed
    private String lastName;

    @Indexed
    private String displayName;

    private String nickName;

    @Indexed
    private String email;

    private String mobile;

    private String workPhone;

    private String extension;

    @Indexed
    private String designation;

    private String designationId;

    @Indexed
    private String department;

    private String departmentId;

    private String grade;

    private String gradeId;

    @Indexed
    private String workLocation;

    private String officeLocation;

    private String seatingLocation;

    private String seatingLocationId;

    private String reportingManagerName;

    @Indexed
    private String reportingManagerZohoId;

    private String reportingManagerEmail;

    private String secondReportingManagerName;

    private String secondReportingManagerZohoId;

    private String employmentStatus;

    private Integer employmentStatusType;

    private List<String> primarySkills;

    private List<String> secondarySkills;

    private String expertise;

    private String aboutMe;

    private String profilePhotoUrl;

    private String branchId;

    private String floorId;

    private String currentSeatId;

    private Boolean active;

    private Boolean deleted;

    private String source;

    private Instant zohoCreatedTime;

    private Instant zohoModifiedTime;

    private Instant lastSyncedAt;
}