package com.frameforward.portfolio.controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.frameforward.portfolio.business.PortfolioNotFound;

@RestControllerAdvice
class PortfolioExceptionHandler {
    @ExceptionHandler(PortfolioNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void notFound() {
    }
}
