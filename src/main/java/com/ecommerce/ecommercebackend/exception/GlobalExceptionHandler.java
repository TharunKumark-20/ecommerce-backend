package com.ecommerce.ecommercebackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // Resource Not Found Exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(

            ResourceNotFoundException exception,

            HttpServletRequest request

    ) {


        ErrorResponse errorResponse =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.NOT_FOUND.value(),

                        exception.getMessage(),

                        request.getRequestURI()

                );


        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.NOT_FOUND
        );
    }





    // Validation Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(

            MethodArgumentNotValidException exception

    ) {


        Map<String, String> errors = new HashMap<>();


        exception.getBindingResult()
                .getAllErrors()
                .forEach(error -> {


                    String fieldName =
                            ((FieldError) error).getField();


                    String message =
                            error.getDefaultMessage();


                    errors.put(
                            fieldName,
                            message
                    );

                });


        return new ResponseEntity<>(
                errors,
                HttpStatus.BAD_REQUEST
        );

    }





    // Authentication Exception (401)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(

            AuthenticationException exception,

            HttpServletRequest request

    ) {


        ErrorResponse errorResponse =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.UNAUTHORIZED.value(),

                        "Authentication failed",

                        request.getRequestURI()

                );


        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.UNAUTHORIZED
        );

    }





    // Access Denied Exception (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(

            AccessDeniedException exception,

            HttpServletRequest request

    ) {


        ErrorResponse errorResponse =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.FORBIDDEN.value(),

                        "Access denied",

                        request.getRequestURI()

                );


        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.FORBIDDEN
        );

    }





    // Generic Exception (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(

            Exception exception,

            HttpServletRequest request

    ) {


        ErrorResponse errorResponse =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.INTERNAL_SERVER_ERROR.value(),

                        exception.getMessage(),

                        request.getRequestURI()

                );


        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.INTERNAL_SERVER_ERROR
        );

    }

}