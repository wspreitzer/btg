package com.btg.website.util;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;

public class TestUtils<T> {

	public  List<T> setupRepository(T...entities) {
		List<T> entityList = new ArrayList<T>();
		Arrays.asList(entities).forEach(anEntity ->{
			entityList.add((T) anEntity);
		});
		return entityList;
	}
}
