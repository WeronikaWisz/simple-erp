package com.simpleerp.simpleerpapp.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(value = {ApiBadRequestException.class})
    public ResponseEntity<Object> handleApiBadRequestException(ApiBadRequestException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
        ApiException apiException = new ApiException(message,
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, badRequest);
    }

    @ExceptionHandler(value = {ApiNotFoundException.class})
    public ResponseEntity<Object> handleApiNotFoundException(ApiNotFoundException e){
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
        ApiException apiException = new ApiException(message,
                notFound,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, notFound);
    }

    @ExceptionHandler(value = {ApiExpectationFailedException.class})
    public ResponseEntity<Object> handleApiExpectationFailedException(ApiExpectationFailedException e){
        HttpStatus expectationFailed = HttpStatus.EXPECTATION_FAILED;
        String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
        ApiException apiException = new ApiException(message,
                expectationFailed,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, expectationFailed);
    }

    @ExceptionHandler(value = {ApiForbiddenException.class})
    public ResponseEntity<Object> handleApiForbiddenException(ApiForbiddenException e){
        HttpStatus forbidden = HttpStatus.FORBIDDEN;
        String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
        ApiException apiException = new ApiException(message,
                forbidden,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, forbidden);
    }

}
