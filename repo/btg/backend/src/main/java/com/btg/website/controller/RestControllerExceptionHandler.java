package com.btg.website.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import javax.validation.ConstraintViolationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.btg.website.errorhandling.RestExceptionHandler;
import com.btg.website.exception.InvalidRequestException;
import com.btg.website.exception.ResourceNotFoundException;

@ControllerAdvice
public class RestControllerExceptionHandler {
	
	//@Autowired
	RestExceptionHandler errorHandler;
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(BAD_REQUEST)
	@ResponseBody
	public ResponseEntity<?> hadleBeanValidationError(MethodArgumentNotValidException methodArgumentNotValidException, WebRequest request) {
		return errorHandler.handleMethodArgumentNotValid(methodArgumentNotValidException, null, BAD_REQUEST, request);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(UNPROCESSABLE_ENTITY)
	@ResponseBody
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
		return errorHandler.handleConstraintViolation(ex, request);
	}
	
	@ResponseStatus(NOT_FOUND)
	@ExceptionHandler(ResourceNotFoundException.class)
	String handleResourceNotFound(ResourceNotFoundException ex) {
		return ex.getMessage();
	}
	
	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(InvalidRequestException.class)
	public void handleBadRequest(InvalidRequestException ex) {
		System.out.println("Invalid account supplied in request");
		ex.printStackTrace();
	}
	
	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public void handleGeneralException(Exception ex){
		System.out.println("An error occurred processing the request");
		ex.printStackTrace();
	}
}