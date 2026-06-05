package com.iexceed.seatmanagement.auth.exceptions;

public class EmployeeInactiveException extends RuntimeException {
    public EmployeeInactiveException(String message) {
        super(message);
    }
}
