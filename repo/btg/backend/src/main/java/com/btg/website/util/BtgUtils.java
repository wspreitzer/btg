package com.btg.website.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.btg.website.repository.builder.BtgSpecificationBuilder;

@Component
public class BtgUtils {
	
	//@Value("${btg.search.regex}")
	//private String regex;

	private static String regex = "(\\p{Punct}?)(\\w+?)(:|<|>)(\\p{Punct}?)(\\w+?)(\\p{Punct}?),";
	
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

	public static <T> BtgSpecificationBuilder<T> buildSearchCriteria(BtgSpecificationBuilder<T> builder, String search) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(search + ",");
		while(matcher.find()) {
			builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(5), matcher.group(4), matcher.group(6));
		}
		return builder;
	}
}