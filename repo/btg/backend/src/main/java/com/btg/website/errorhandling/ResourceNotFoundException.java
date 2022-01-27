package com.btg.website.errorhandling;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.BtgUtils;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException() {
		super("Sorry no results were returned.  Please try again.");
	}
	
	public ResourceNotFoundException(String entity, Long id) {
		super(BtgUtils.createExceptionMessage(entity, id));
	}
	
	public ResourceNotFoundException(String entity, Specification<?> spec) {
		super(BtgUtils.createExceptionMessage(entity, (BtgSpecification<?>) spec));
	}
}