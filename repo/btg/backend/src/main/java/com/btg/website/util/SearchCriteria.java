package com.btg.website.util;

public class SearchCriteria {
	private String key;
	private SearchOperation operation;
	private Object value;
	private boolean orPredicate;
	
	public SearchCriteria(final String key, final SearchOperation operation, final Object value) {
		this.key = key;
		this.operation = operation;
		this.value = value;
	}
	
	public SearchCriteria(final String orPredicate, final String key, final SearchOperation operation, final Object value) {
		this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_FLAG);
		this.key = key;
		this.operation = operation;
		this.value = value;
	}
	
	public SearchCriteria(final String key, final String operation, final String prefix, final String value, final String suffix) {
		SearchOperation op = SearchOperation.getSimpleOpertion(operation);
		if(op != null) {
			if(op == SearchOperation.EQUALITY) {
				final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.WILD_CARD);
				final boolean endsWithAsterisk = suffix != null && suffix.contains(SearchOperation.WILD_CARD);
				if(startWithAsterisk && endsWithAsterisk) {
					op = SearchOperation.CONTAINS;
				} else if (startWithAsterisk) {
					op = SearchOperation.ENDS_WITH;
				} else if (endsWithAsterisk) {
					op = SearchOperation.STARTS_WITH;
				}
			}
		}
		this.key = key;
		this.operation = op;
		this.value = value;
	}
	
	public SearchCriteria(final String orPredicate, final String key, final String operation, final String prefix, final String value, final String suffix) {
		SearchOperation op = SearchOperation.getSimpleOpertion(operation);
		if(op != null) {
			if(op == SearchOperation.EQUALITY) {
				final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.WILD_CARD);
				final boolean endsWithAsterisk = suffix != null && suffix.contains(SearchOperation.WILD_CARD);
				if(startWithAsterisk && endsWithAsterisk) {
					op = SearchOperation.CONTAINS;
				} else if (startWithAsterisk) {
					op = SearchOperation.ENDS_WITH;
				} else if (endsWithAsterisk) {
					op = SearchOperation.STARTS_WITH;
				}
			}
		}
		
		this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_FLAG);
		this.key = key;
		this.operation = op;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public SearchOperation getOperation() {
		return operation;
	}

	public void setOperation(final SearchOperation operation) {
		this.operation = operation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(final Object value) {
		this.value = value;
	}

	public boolean isOrPredicate() {
		return orPredicate;
	}

	public void setOrPredicate(final boolean orPredicate) {
		this.orPredicate = orPredicate;
	}
}
