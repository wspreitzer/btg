package com.btg.website.errorhandling;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.btg.website.controller.AccountNotFoundException;



public class RestExceptionHandler extends ResponseEntityExceptionHandler{

	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		List<String> globalErrors = ex.getBindingResult().getGlobalErrors()
				.stream()
				.map(error -> error.getObjectName() + ": " + error.getDefaultMessage())
				.collect(Collectors.toList());
		
		List<ApiPropertyError> fieldErrors = ex.getBindingResult().getFieldErrors()
				.stream()
				.map(this::toPropertyError)
				.collect(toList());
		
		ApiError apiError = ApiError.builder()
				.message("Validation error")
				.status(ex.getParameter().hasParameterAnnotation(RequestBody.class) ? HttpStatus.UNPROCESSABLE_ENTITY : HttpStatus.BAD_REQUEST)
				.details(Stream.concat(globalErrors.stream(), fieldErrors.stream()).collect(toList()))
				.build();
		return handleExceptionInternal(ex, apiError, headers, status, request);
	}
	
	@ExceptionHandler(ConstraintViolationException.class) 
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
		List<ApiPropertyError> errors = ex.getConstraintViolations()
				.stream()
				.map(this::toPropertyError)
				.collect(toList());
		
		ApiError apiError = ApiError.builder()
				.message("Validation Error")
				.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.details(errors)
				.build();
		return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
	}

	public ResponseEntity<Object> handleResourceNotFound(AccountNotFoundException ex, WebRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private ApiPropertyError toPropertyError(ConstraintViolation<?> violation) {
		return ApiPropertyError.builder()
				.property(violation.getPropertyPath().toString())
				.message(violation.getMessage())
				.invalidValue(violation.getInvalidValue())
				.build();
	}
	
	private ApiPropertyError toPropertyError(FieldError fieldError) {
		return ApiPropertyError.builder()
				.property(fieldError.getField())
				.message(fieldError.getDefaultMessage())
				.invalidValue(fieldError.getRejectedValue())
				.build();
	}

}