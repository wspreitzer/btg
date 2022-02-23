package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.btg.website.controller.OrderLineItemRestController;
import com.btg.website.model.OrderLineItem;

@Component
public class OrderLineItemModelAssembler implements RepresentationModelAssembler<OrderLineItem, EntityModel<OrderLineItem>> {
	
	@Override
	public EntityModel<OrderLineItem> toModel(OrderLineItem orderLineItem) {
		return EntityModel.of(orderLineItem, 
				linkTo(methodOn(OrderLineItemRestController.class).getOrderLineItemById(orderLineItem.getId())).withSelfRel(),
				linkTo(methodOn(OrderLineItemRestController.class).getOrderLineItems(orderLineItem.getOrder().getId())).withRel("orderLineItems"));
	}
}
