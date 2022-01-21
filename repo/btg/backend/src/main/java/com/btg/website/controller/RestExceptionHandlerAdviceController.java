package com.btg.website.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.btg.website.errorhandling.RestExceptionHandler;



@ControllerAdvice
public class RestExceptionHandlerAdviceController {
	
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
}