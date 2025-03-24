package com.svalero.apievents.exception;

public class EventCategoryNotFoundException extends RuntimeException {
    public EventCategoryNotFoundException(String message) {
        super(message);
    }
}
