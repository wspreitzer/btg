package com.btg.website.errorhandling;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiPropertyError {

	private String property;
	private String message;
	
	@JsonInclude
	private Object invalidValue;
}