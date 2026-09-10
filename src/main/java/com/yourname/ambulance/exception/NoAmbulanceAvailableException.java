package com.yourname.ambulance.exception;

public class NoAmbulanceAvailableException extends RuntimeException {
    public NoAmbulanceAvailableException(String message) { super(message); }
}