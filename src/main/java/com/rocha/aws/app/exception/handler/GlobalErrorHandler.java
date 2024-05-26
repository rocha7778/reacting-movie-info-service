package com.rocha.aws.app.exception.handler;

import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {
	
	
	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<String> handlerRequestBodyError(WebExchangeBindException ex){
		log.error("Error: {}", ex.getMessage());
		
		var error = ex.getBindingResult()
		.getAllErrors()
		.stream()
		.map(DefaultMessageSourceResolvable::getDefaultMessage)
		.sorted()
		.collect(Collectors.joining(","));
		
		log.error("Error: {}", error);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		
	}

}
