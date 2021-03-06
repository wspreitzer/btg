package com.btg.website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;

@Controller
@RequestMapping("/btg")
public class BtgController<T> {
	
	protected BtgSpecification<T> spec;
	
	protected BtgSpecificationBuilder<T> builder;
}