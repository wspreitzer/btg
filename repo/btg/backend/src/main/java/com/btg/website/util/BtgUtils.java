package com.btg.website.util;

import com.btg.website.repository.specification.BtgSpecification;

public class BtgUtils {
	
	public static String createExceptionMessage(String entity, Long id) {
		StringBuilder sb = new StringBuilder();
		sb.append("Sorry no ");
		sb.append(entity.getClass().getName());
		sb.append(" with id ");
		sb.append(id);
		sb.append(" was found in the system.  Please try again.");
		return sb.toString();
	}
	
	public static String createExceptionMessage(String entity, BtgSpecification<?> spec) {
		StringBuilder sb = new StringBuilder();
		sb.append("Sorry no ");
		sb.append(entity.getClass().getName());
		sb.append(" with ");
		sb.append(spec.getCriteria().getKey());
		sb.append(" ");
		sb.append(spec.getCriteria().getValue());
		sb.append(" was found in the system.  Please try again.");
		return sb.toString();
	}
}
