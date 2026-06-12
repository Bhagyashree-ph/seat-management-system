package com.iexceed.seatmanagement.floors.exceptions;

public class FloorNotFoundException extends RuntimeException {
    public FloorNotFoundException(String message) {
        super(message);
    }
}
