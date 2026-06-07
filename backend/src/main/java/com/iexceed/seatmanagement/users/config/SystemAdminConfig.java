package com.iexceed.seatmanagement.users.config;

import lombok.Data;

@Data
public class SystemAdminConfig {

    private String employeeCode;

    private String displayName;

    private String email;

    private String username;

    private String password;
}