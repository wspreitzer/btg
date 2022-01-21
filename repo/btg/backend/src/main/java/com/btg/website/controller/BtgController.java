package com.btg.website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;

@Controller
@RequestMapping("/btg/v1")
public class BtgController<T> {
	
	protected BtgSpecification<T> spec;
	
	protected BtgSpecificationBuilder<T> builder;
}