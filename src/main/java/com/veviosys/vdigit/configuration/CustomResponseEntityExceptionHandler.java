package com.veviosys.vdigit.configuration;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ResponseBody 
    @ExceptionHandler(ArithmeticException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)

    public Error handleValidationException(ArithmeticException e) {
        // Optionally do additional things with the exception, for example map
        // individual field errors (from e.getBindingResult()) to the Error object
       
        return new Error("Invalid data");
    }
}
