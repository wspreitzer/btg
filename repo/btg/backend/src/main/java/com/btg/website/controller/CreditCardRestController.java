package com.btg.website.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.exception.ResourceNotFoundException;
import com.btg.website.model.CreditCard;
import com.btg.website.repository.CreditCardRepository;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.BtgUtils;
import com.btg.website.util.CreditCardModelAssembler;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

@RestController
public class CreditCardRestController extends BtgRestController<CreditCard> {
	
	private List<CreditCard> creditCards;
	private final CreditCardModelAssembler assembler;
	
	@Autowired private CreditCardRepository creditCardRepo;

	@Autowired
	public CreditCardRestController(CreditCardModelAssembler assembler) {
		builder = new BtgSpecificationBuilder<CreditCard>();
		this.assembler = assembler;
	}
	
	@GetMapping("/rest/creditCards/")
	public CollectionModel<EntityModel<CreditCard>> getUserCreditCards() {
		//customer = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("userName", SearchOperation.EQUALITY, "userName"))).get(0);
		List<EntityModel<CreditCard>> creditCards = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("customer", SearchOperation.EQUALITY, 0))).stream().map(assembler::toModel).collect(Collectors.toList());
		if (creditCards.size() > 0) {
			return CollectionModel.of(creditCards, linkTo(methodOn(CreditCardRestController.class).getUserCreditCards()).withSelfRel());
		} else {
			throw new ResourceNotFoundException();
		}
	}
	
	@GetMapping("/rest/creditCard/{id}")
	public EntityModel<CreditCard> getCreditCardById(@PathVariable Long id) {
		CreditCard card = creditCardRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Credit Card", id));
		return assembler.toModel(card);
	}

	@GetMapping("/rest/creditCardsBySpecification/")
	public CollectionModel<EntityModel<CreditCard>> getUserCreditCardsBySpecification(@RequestParam(value="search") String search) throws Exception {
		builder = BtgUtils.buildSearchCriteria(builder, search);
		builder.with("customer", ":", 0, "", "");
		
		Specification<CreditCard> spec =  builder.build(searchCriteria -> new BtgSpecification<CreditCard>((SearchCriteria) searchCriteria));
		List<EntityModel<CreditCard>> creditCards = creditCardRepo.findAll(spec).stream().map(assembler::toModel).collect(Collectors.toList());
		if(creditCards.size() > 0) {
			return CollectionModel.of(creditCards, linkTo(methodOn(CreditCardRestController.class).getUserCreditCardsBySpecification(search)).withSelfRel());
		} else {
			throw new ResourceNotFoundException("Credit Card", builder);
		}
	}
	
	@GetMapping("/rest/creditCards/count")
	public ResponseEntity<Long> getCountOfUserCreditCards() {
		/* 
		 * TO DO Implement security context to get user to get userId to search for customer to get creditcards" 
		 *	customer = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("userName", SearchOperation.EQUALITY, 0))).get(0);
		 */
	
		
		creditCards = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("customer", SearchOperation.EQUALITY, 0)));
		if (creditCards.size() > 0) {
			return new ResponseEntity<Long>((long) creditCards.size(), HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException();
		}
	}
	
	@PostMapping(value = "/rest/creditCard")
	public CreditCard createAccount(@RequestBody CreditCard creditCard, HttpServletResponse response, HttpServletRequest request) {
		CreditCard savedCreditCard = creditCardRepo.save(creditCard);
		response.setStatus(HttpStatus.CREATED.value());
		response.setHeader("Location", String.format("%s/btg/rest/creditCards/%s", request.getContextPath(), savedCreditCard.getId(), null));
		return savedCreditCard;
	}
	
	@DeleteMapping("/rest/creditCards/{id}")
	public ResponseEntity<List<CreditCard>> deleteCustomerCreditCardById(@PathVariable Long id, HttpServletResponse response, HttpServletRequest request) {
		creditCardRepo.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}