package com.yourname.ambulance.exception;

public class AmbulanceNotFoundException extends RuntimeException {
    public AmbulanceNotFoundException(String message) { super(message); }
}