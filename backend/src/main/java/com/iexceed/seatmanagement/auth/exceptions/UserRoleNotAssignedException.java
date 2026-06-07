package com.iexceed.seatmanagement.auth.exceptions;

public class UserRoleNotAssignedException extends RuntimeException {
    public UserRoleNotAssignedException(String message) {
        super(message);
    }
}
