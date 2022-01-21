package com.btg.website.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(AccountNotFoundException.class)
	public void handleNotFound(AccountNotFoundException ex) {
		System.out.println("Requested account not found");
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(InvalidAccountRequestException.class)
	public void handleBadRequest(InvalidAccountRequestException ex) {
		System.out.println("Invalid account supplied in request");
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public void handleGeneralError(Exception ex) {
		System.out.println("An error occurred processing request");
		ex.printStackTrace();
	}
}