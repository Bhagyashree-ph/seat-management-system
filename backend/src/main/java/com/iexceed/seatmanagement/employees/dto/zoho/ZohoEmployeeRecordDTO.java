package com.iexceed.seatmanagement.employees.dto.zoho;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZohoEmployeeRecordDTO {

    @JsonProperty("EmployeeID")
    private String employeeId;

    @JsonProperty("Zoho_ID")
    private String zohoId;

    @JsonProperty("ZUID")
    private String zuid;

    @JsonProperty("FirstName")
    private String firstName;

    @JsonProperty("LastName")
    private String lastName;

    @JsonProperty("Full_Name2")
    private String fullName;

    @JsonProperty("Nick_Name")
    private String nickName;

    @JsonProperty("EmailID")
    private String email;

    @JsonProperty("Mobile")
    private String mobile;

    @JsonProperty("Work_phone")
    private String workPhone;

    @JsonProperty("Extension")
    private String extension;

    @JsonProperty("Designation")
    private String designation;

    @JsonProperty("Designation.ID")
    private String designationId;

    @JsonProperty("Department")
    private String department;

    @JsonProperty("Department.ID")
    private String departmentId;

    @JsonProperty("Grade")
    private String grade;

    @JsonProperty("Grade.id")
    private String gradeId;

    @JsonProperty("LocationName")
    private String officeLocation;

    @JsonProperty("Work_location")
    private String workLocation;

    @JsonProperty("Seating_Location")
    private String seatingLocation;

    @JsonProperty("Seating_Location.id")
    private String seatingLocationId;

    @JsonProperty("Reporting_To")
    private String reportingManagerName;

    @JsonProperty("Reporting_To.ID")
    private String reportingManagerZohoId;

    @JsonProperty("Reporting_To.MailID")
    private String reportingManagerEmail;

    @JsonProperty("Second_Reporting_To")
    private String secondReportingManagerName;

    @JsonProperty("Second_Reporting_To.ID")
    private String secondReportingManagerZohoId;

    @JsonProperty("Employeestatus")
    private String employmentStatus;

    @JsonProperty("Employeestatus.type")
    private Integer employmentStatusType;

    @JsonProperty("Skills")
    private String skills;

    @JsonProperty("Secondary_Skills")
    private String secondarySkills;

    @JsonProperty("Expertise")
    private String expertise;

    @JsonProperty("AboutMe")
    private String aboutMe;

    @JsonProperty("Photo_downloadUrl")
    private String profilePhotoUrl;

    @JsonProperty("CreatedTime")
    private String createdTime;

    @JsonProperty("ModifiedTime")
    private String modifiedTime;
}