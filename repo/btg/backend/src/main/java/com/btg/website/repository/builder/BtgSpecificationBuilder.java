package com.btg.website.repository.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.btg.website.exception.InvalidRequestException;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

@Component
public class BtgSpecificationBuilder<T> {

	private List<SearchCriteria> params;
	
	public BtgSpecificationBuilder() {
		this.params = new ArrayList<SearchCriteria>();
	}
	
	public List<SearchCriteria> getParams() {
		return params;
	}
	
	public BtgSpecificationBuilder<T> with(final String orIndicator, final String key, 
			final String operation, final Object value, final String prefix, final String suffix) {
		SearchOperation op = SearchOperation.getSimpleOpertion(operation.charAt(0));
		if(op != null) {
			if (op == SearchOperation.EQUALITY) {
				final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.WILD_CARD);
				final boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.WILD_CARD);
				if (startWithAsterisk && endWithAsterisk) {
					op = SearchOperation.CONTAINS;
				} else if (startWithAsterisk) {
					op = SearchOperation.ENDS_WITH;
				} else if (endWithAsterisk) {
					op = SearchOperation.STARTS_WITH;
				}
			}
		}
		params.add(new SearchCriteria(orIndicator, key, op, value));
		return this;
	}

	public BtgSpecificationBuilder<T> with(final String key, final String operation, final Object value, 
			final String prefix, final String suffix) {
		return this.with(null, key, operation, value, prefix, suffix);
	}
	
	public  Specification<T> build(Function<SearchCriteria, BtgSpecification<T>> converter) {
		Specification<T> result;
		if(params.size() > 0) {
			final List<Specification<T>> specs = params.stream()
					.map(converter)
					.collect(Collectors.toCollection(ArrayList::new));
			result = specs.get(0);
			for(int i = 1; i < specs.size(); i++) {
				result = params.get(i)
						.isOrPredicate()
							? Specification.where(result)
									.or(specs.get(i))
							: Specification.where(result)
									.and(specs.get(i));
						? Specification.where(result)
								.or(specs.get(i))
								: Specification.where(result)
								.and(specs.get(i));
			}
		} else {
			throw new InvalidRequestException();
		}
		return result;
	}
}