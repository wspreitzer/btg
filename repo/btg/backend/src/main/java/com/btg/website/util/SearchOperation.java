package com.btg.website.util;

public enum SearchOperation {

EQUALITY, NEGATION, GREATER_THAN, GREATER_THAN_EQUAL, LESS_THAN, LESS_THAN_EQUAL, LIKENESS, CONTAINS, STARTS_WITH, ENDS_WITH;
	
	public static final String[] SIMPLE_OPERATION_SET = {":", "!", ">", ">=", "<=", "<", "~"};
	
	public static final String OR_FLAG = "'";
	
	public static final String WILD_CARD = "*";
	
	public static final String OR_OPERATOR = "OR";
	
	public static final String AND_OPERATOR = "AND";
	
	public static final String LEFT_PAREN = "(";
	
	public static final String RIGHT_PAREN = ")";
	
	public static final SearchOperation getSimpleOpertion(String input) {
		SearchOperation retVal;
		switch(input) {
		case ":":
			retVal = EQUALITY;
			break;
		case "!":
			retVal = NEGATION;
			break;
		case ">":
			retVal = GREATER_THAN;
			break;
		case "<":
			retVal = LESS_THAN;
			break;
		case "~":
			retVal = LIKENESS;
			break;
		case ">=":
			retVal = GREATER_THAN_EQUAL;
			break;
		case "<=":
			retVal = LESS_THAN_EQUAL;
			break;
		default:
			retVal = null;
			break;
		}
		return retVal;
	}
}
