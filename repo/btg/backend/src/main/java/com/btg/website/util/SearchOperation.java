package com.btg.website.util;

public enum SearchOperation {

EQUALITY, NEGATION, GREATER_THAN, LESS_THAN, LIKE, CONTAINS, STARTS_WITH, ENDS_WITH;
	
	public static final String[] SIMPLE_OPERATION_SET = {":", "!", ">", "<", "~"};
	
	public static final String OR_FLAG = "'";
	
	public static final String WILD_CARD = "*";
	
	public static final String OR_OPERATOR = "OR";
	
	public static final String AND_OPERATOR = "AND";
	
	public static final String LEFT_PAREN = "(";
	
	public static final String RIGHT_PAREN = ")";
	
	public static final SearchOperation getSimpleOpertion(char input) {
		SearchOperation retVal;
		switch(input) {
		case ':':
			retVal = EQUALITY;
			break;
		case '!':
			retVal = NEGATION;
			break;
		case '>':
			retVal = GREATER_THAN;
			break;
		case '<':
			retVal = LESS_THAN;
			break;
		case '~':
			retVal = LIKE;
			break;
		default:
			retVal = null;
			break;
		}
		return retVal;
	}
}
