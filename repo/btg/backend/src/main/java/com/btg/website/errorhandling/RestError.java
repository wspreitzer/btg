package com.btg.website.errorhandling;

public class RestError {

	private final String field;
	private final String message;
	
	public RestError(String field, String message) {
		this.field = field;
		this.message = message;
	}

	public String getField() {
		return field;
	}

	public String getMessage() {
		return message;
	}
}