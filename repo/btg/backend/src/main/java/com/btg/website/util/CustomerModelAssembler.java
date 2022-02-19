package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.btg.website.controller.CustomerRestController;
import com.btg.website.model.Customer;

@Component
public class CustomerModelAssembler implements RepresentationModelAssembler<Customer, EntityModel<Customer>> {
	
	@Override
	public EntityModel<Customer> toModel(Customer customer) {
		return EntityModel.of(customer, 
				linkTo(methodOn(CustomerRestController.class).getCustomerById(customer.getId())).withSelfRel(),
				linkTo(methodOn(CustomerRestController.class).getCustomers()).withRel("customers"));
	}
}