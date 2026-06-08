package com.iexceed.seatmanagement.branches.exceptions;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException(String message) {
        super(message);
    }
}
