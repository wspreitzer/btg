package com.btg.website.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

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
	
	@PostMapping("/rest/supportTicket/")
	public ResponseEntity<EntityModel<SupportTicket>> createSupportTicket(@RequestBody SupportTicket ticket, HttpServletResponse response, HttpServletRequest request) {
		SupportTicket newTicket = supportTicketRepo.save(ticket);
		return ResponseEntity
				.created(linkTo(methodOn(SupportTicketRestController.class).getSupportTicketById(newTicket.getId())).toUri())
				.header("Location", String.format("%s/btg/rest/supportTicket/%s", request.getContextPath(), newTicket.getId(), null))
				.body(assembler.toModel(newTicket));
	}
	
	@GetMapping("/rest/supportTicket/{id}")
	public EntityModel<SupportTicket> getSupportTicketById(@PathVariable Long id) {
		SupportTicket ticket = supportTicketRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("SupportTicket", id));
		return EntityModel.of(ticket,
				linkTo(methodOn(SupportTicketRestController.class).getSupportTicketById(id)).withSelfRel());
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

	@PatchMapping(path = "/rest/updateSupportTicket/{id}", consumes = "application/json-patch+json")
	public ResponseEntity<EntityModel<SupportTicket>> updateSupportTicketField(@PathVariable Long id, @RequestBody JsonPatch patch) {
		ResponseEntity<EntityModel<SupportTicket>> retVal;
		try {
			SupportTicket foundTicket = supportTicketRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Support Ticket", id));
			SupportTicket updatedTicket = applyPatchToSupportTicket(patch, foundTicket);
			retVal = ResponseEntity.ok(assembler.toModel(supportTicketRepo.save(updatedTicket)));
		} catch(JsonPatchException | JsonProcessingException e) {
			retVal = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			e.printStackTrace();
		} catch(ResourceNotFoundException e) {
			retVal = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return retVal;
	}
	
	@DeleteMapping("/rest/supportTicket/{id}")
	public ResponseEntity<?> deleteSupportTicketById(@PathVariable Long id) {
		supportTicketRepo.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	private SupportTicket applyPatchToSupportTicket(JsonPatch patch, SupportTicket targetSupportTicket) throws JsonPatchException, JsonProcessingException {
		JsonNode patched = patch.apply(objectMapper.convertValue(targetSupportTicket, JsonNode.class));
		return objectMapper.treeToValue(patched, SupportTicket.class);
	}
}