package com.moveinsync.metrobooking.exception;

public class StopNotFoundException extends RuntimeException {
    public StopNotFoundException(String message) {
        super(message);
    }
}