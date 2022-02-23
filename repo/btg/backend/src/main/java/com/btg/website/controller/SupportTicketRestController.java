package com.btg.website.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.exception.ResourceNotFoundException;
import com.btg.website.model.Customer;
import com.btg.website.model.SupportTicket;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.SupportTicketRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;
import com.btg.website.util.SupportTicketModelAssembler;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class SupportTicketRestController extends BtgRestController<SupportTicket> {
	
	@Autowired private SupportTicketRepository supportTicketRepo;
	@Autowired private CustomerRepository customerRepo;
	
	private final SupportTicketModelAssembler assembler;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	SupportTicketRestController(SupportTicketModelAssembler assembler) {
		builder = new BtgSpecificationBuilder<SupportTicket>();
		this.assembler = assembler;
	}
	
	@GetMapping("/rest/supportTicket/{id}")
	public EntityModel<SupportTicket> getSupportTicketById(@PathVariable Long id) {
		return null;
	}
	
	@GetMapping("/rest/supportTickets")
	public CollectionModel<EntityModel<SupportTicket>> getAllCustomerTickets() {
		Specification<Customer> spec = new BtgSpecification<Customer>(new SearchCriteria("userName", SearchOperation.EQUALITY, "user1"));
		Customer customer = customerRepo.findAll(spec).get(0);
		Specification<SupportTicket> supportTicketSpec = new BtgSpecification<>(new SearchCriteria("customer", SearchOperation.EQUALITY, customer));
		List<EntityModel<SupportTicket>> supportTickets = supportTicketRepo.findAll(supportTicketSpec).stream().map(assembler::toModel).collect(toList());
		if(supportTickets.size() > 0) {
			return CollectionModel.of(supportTickets,
					linkTo(methodOn(SupportTicketRestController.class).getAllCustomerTickets()).withSelfRel());
		} else {
			throw new ResourceNotFoundException();
		}
	}
}