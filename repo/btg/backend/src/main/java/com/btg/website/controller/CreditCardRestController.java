package com.btg.website.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.btg.website.errorhandling.ResourceNotFoundException;
import com.btg.website.model.CreditCard;
import com.btg.website.model.Customer;
import com.btg.website.repository.CreditCardRepository;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;

@Controller
public class CreditCardRestController extends BtgRestController<CreditCard> {

	
	private BtgSpecificationBuilder<CreditCard> builder = new BtgSpecificationBuilder<>();
	
	@Autowired
	private CustomerRepository customerRepo;
	
	@Autowired
	private CreditCardRepository creditCardRepo;
	
	@GetMapping("/rest/creditcards/{type}")
	public ResponseEntity<List<CreditCard>> getUsersCreditCardsByType(@PathVariable String type) {
		List<CreditCard> creditCards = creditCardRepo.findAll(builder.with("type", ":", type, "", "").build(searchCriteria -> new BtgSpecification<CreditCard>((SearchCriteria) searchCriteria)));
		if (creditCards != null) {
			return new ResponseEntity<List<CreditCard>>(creditCards, HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException();
		}
	}
	
	@GetMapping("/rest/creditcards/{username}")
	public ResponseEntity<List<CreditCard>> getUsersCreditCards(@PathVariable String username) {
		BtgSpecificationBuilder<Customer> customerBuilder = new BtgSpecificationBuilder<Customer>();
		List<Customer> customers = customerRepo.findAll(customerBuilder.with("userName", ":", username, "", "").build(searchCriteria -> new BtgSpecification<Customer>((SearchCriteria) searchCriteria )));
		List<CreditCard> creditCards = creditCardRepo.findAll(builder.with("customerId", ":", customers.get(0).getId(), "", "").build(searchCriteria -> new BtgSpecification<CreditCard>((SearchCriteria) searchCriteria)));
		
		if (creditCards != null) {
			return new ResponseEntity<List<CreditCard>>(creditCards, HttpStatus.OK);
		} else {
			System.out.println("HOLY SHIT WE MADE IT HERE!!!!");
			throw new ResourceNotFoundException();
		}
	}
}
