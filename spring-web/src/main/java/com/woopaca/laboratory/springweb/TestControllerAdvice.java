package com.woopaca.laboratory.springweb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TestControllerAdvice {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        log.error("An error occurred: {}", e.getMessage());
        return "An error occurred: " + e.getMessage();
    }
}
