package com.btg.website.util;

import com.btg.website.repository.builder.BtgSpecificationBuilder;

public class BtgUtils {
	
	public static String createExceptionMessage(String entity, Long id) {
		StringBuilder sb = new StringBuilder();
		sb.append("Sorry no ");
		sb.append(entity);
		sb.append(" with id ");
		sb.append(id);
		sb.append(" was found in the system.  Please try again.");
		return sb.toString();
	}
	
	public static String createExceptionMessage(String entity, BtgSpecificationBuilder<?> builder) {
		StringBuilder sb = new StringBuilder();
		sb.append("Sorry no ");
		sb.append(entity);
		sb.append(" matched the following  search criteria; ");
		builder.getParams().forEach(aParam -> {
			sb.append(aParam.getKey());
			sb.append(" ");
			sb.append(aParam.getOperation());
			sb.append(" ");
			sb.append(aParam.getValue());
			sb.append(" ");
		});
		sb.append(". Please try again.");
		return sb.toString();
	}
}