package com.btg.website.repository.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import com.btg.website.util.SearchCriteria;

public class BtgSpecification<T> implements Specification<T> {
	private static final long serialVersionUID = 1L;
	
	private SearchCriteria criteria;
	
	public BtgSpecification(final SearchCriteria criteria) {
		this.criteria = criteria;
	}
	
	public SearchCriteria getCriteria() {
		return this.criteria;
	}
	
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		Predicate retVal;
		StringBuffer sb = new StringBuffer();
		switch (criteria.getOperation()) {
		case EQUALITY:
			retVal = builder.equal(root.get(criteria.getKey()), criteria.getValue());
		case NEGATION:
			retVal = builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
		case GREATER_THAN:
			retVal = builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
		case LESS_THAN:
			retVal = builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
		case LIKE:
			retVal = builder.like(root.get(criteria.getKey()), criteria.getValue().toString());
		case STARTS_WITH:
			sb.append(criteria.getValue());
			sb.append("%");
			retVal = builder.like(root.get(criteria.getKey()), sb.toString());
		case ENDS_WITH:
			sb.append("%");
			sb.append(criteria.getValue());
			retVal = builder.like(root.get(criteria.getKey()), sb.toString());
		case CONTAINS:
			sb.append("%");
			sb.append(criteria.getValue());
			sb.append("%");
			retVal = builder.like(root.get(criteria.getKey()), sb.toString());
		default:
			retVal = null;
		}
		return retVal;
	}
}