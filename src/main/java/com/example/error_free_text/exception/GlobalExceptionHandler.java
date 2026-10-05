package com.example.error_free_text.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {

        return Map.of(
                "errorMessage", exception.getMessage(),
                "errorCode", 40001,
                "timestamp", LocalDateTime.now(),
                "path", request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return Map.of(
                "errorMessage", message,
                "errorCode", 40002,
                "timestamp", LocalDateTime.now(),
                "path", request.getRequestURI());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleTaskNotFound(
            TaskNotFoundException exception,
            HttpServletRequest request) {

        return Map.of(
                "errorMessage", exception.getMessage(),
                "errorCode", 40401,
                "timestamp", LocalDateTime.now(),
                "path", request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(
            Exception exception,
            HttpServletRequest request) {

        return Map.of(
                "errorMessage", exception.getMessage(),
                "errorCode", 50000,
                "timestamp", LocalDateTime.now(),
                "path", request.getRequestURI());
    }
}