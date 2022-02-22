package com.btg.website.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.exception.ResourceNotFoundException;
import com.btg.website.model.OrderLineItem;
import com.btg.website.repository.OrderLineItemRepository;
import com.btg.website.repository.OrderRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.OrderLineItemModelAssembler;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

@RestController
public class OrderLineItemRestController extends BtgRestController<OrderLineItem> {

	@Autowired private OrderRepository orderRepo;
	@Autowired private OrderLineItemRepository orderLineItemRepo;
	private List<OrderLineItem> orderLineItemsList;
	
	private OrderLineItemModelAssembler assembler;
	
	@Autowired
	OrderLineItemRestController(OrderLineItemModelAssembler assembler) {
		builder = new BtgSpecificationBuilder<OrderLineItem>();
		this.assembler = assembler;
	}
	
	@GetMapping("/rest/orderLineItem/{id}")
	public EntityModel<OrderLineItem> getOrderLineItemById(@PathVariable Long id) {
		OrderLineItem orderLineItem = orderLineItemRepo.findById(id).get();
		return assembler.toModel(orderLineItem);
	}
	
	@GetMapping("/rest/orderLineItems/{id}")
	public CollectionModel<EntityModel<OrderLineItem>> getOrderLineItems(@PathVariable Long id) {
		List<EntityModel<OrderLineItem>> lineItems = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("order", SearchOperation.EQUALITY, id))).stream().map(assembler::toModel).collect(toList());
		if (lineItems.size() > 0) {
			return CollectionModel.of(lineItems, 
					linkTo(methodOn(OrderLineItemRestController.class).getOrderLineItems(id)).withSelfRel());
		} else {
			throw new ResourceNotFoundException();
		}
	}
}
