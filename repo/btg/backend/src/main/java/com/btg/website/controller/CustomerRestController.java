package com.btg.website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.model.Customer;
import com.btg.website.repository.CustomerRepository;

@RestController
public class CustomerRestController extends BtgRestController{

	@Autowired private CustomerRepository customerRepo;
	
	
	@GetMapping("/rest/customers/{id}")
	public EntityModel<Customer> getCustomerById(@PathVariable Long id) {
		return null;
	}
	
	@GetMapping("/admin/rest/customers")
	public CollectionModel<EntityModel<Customer>> getCustomers() {
		return null;
	}
}
