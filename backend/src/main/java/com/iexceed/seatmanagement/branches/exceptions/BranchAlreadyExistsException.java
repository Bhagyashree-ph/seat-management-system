package com.iexceed.seatmanagement.branches.exceptions;

public class BranchAlreadyExistsException extends RuntimeException {
    public BranchAlreadyExistsException(String message) {
        super(message);
    }
}
