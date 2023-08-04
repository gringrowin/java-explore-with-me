package ru.practicum.exception;

public class ForbiddenParameterException extends RuntimeException {
    public ForbiddenParameterException(String message) {
        super(message);
    }
}
