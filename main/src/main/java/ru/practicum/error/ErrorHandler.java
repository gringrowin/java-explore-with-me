package ru.practicum.error;


import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.exception.ForbiddenParameterException;
import ru.practicum.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("ErrorResponse.handleNotFoundException: {} ", e.toString());
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(timeStampToString(LocalDateTime.now()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleForbiddenParameterException(final ForbiddenParameterException e) {
        log.error("ErrorResponse.handleForbiddenParameterException: {} ", e.toString());
        return ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.name())
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(timeStampToString(LocalDateTime.now()))
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.error("ErrorResponse.handleDataIntegrityViolationException: {} ", e.toString());
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .timestamp(timeStampToString(LocalDateTime.now()))
                .build();
    }

    @ExceptionHandler({
                    MethodArgumentTypeMismatchException.class,
                    ConstraintViolationException.class,
                    MethodArgumentNotValidException.class,
                    MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final Throwable e) {
        log.error("ErrorResponse.handleValidationException: {}", e.toString());
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(timeStampToString(LocalDateTime.now()))
                .build();
    }



    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnknownException(final Throwable e) {
        log.error("ErrorResponse.handleUnknownException: {}", e.toString());
        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .reason("Unknown exception.")
                .message(e.getMessage())
                .timestamp(timeStampToString(LocalDateTime.now()))
                .build();
    }

    private String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String timeStampToString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
