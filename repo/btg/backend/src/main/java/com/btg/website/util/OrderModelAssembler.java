package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.btg.website.controller.OrderRestController;
import com.btg.website.exception.InvalidRequestException;
import com.btg.website.model.Order;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {
	
	@Override
	public EntityModel<Order> toModel(Order order) {
		EntityModel<Order> orderModel = EntityModel.of(order,
				linkTo(methodOn(OrderRestController.class).getCustomerOrdersById(order.getId())).withSelfRel(),
				linkTo(methodOn(OrderRestController.class).getCustomerOrders()).withRel("orders"));
		switch(order.getOrderStatus()) {
		case NEW:
			orderModel.add(linkTo(methodOn(OrderRestController.class).picking(order.getId())).withRel("picking"));
			orderModel.add(linkTo(methodOn(OrderRestController.class).cancel(order.getId())).withRel("cancel"));
			break;
		case PICKING:
			orderModel.add(linkTo(methodOn(OrderRestController.class).picked(order.getId())).withRel("picked"));
			orderModel.add(linkTo(methodOn(OrderRestController.class).cancel(order.getId())).withRel("cancel"));			
			break;
		case PICKED:
			orderModel.add(linkTo(methodOn(OrderRestController.class).loading(order.getId())).withRel("loading"));			
			break;
		case LOADING:
			orderModel.add(linkTo(methodOn(OrderRestController.class).loaded(order.getId())).withRel("loaded"));			
			break;
		case LOADED:
			orderModel.add(linkTo(methodOn(OrderRestController.class).shipped(order.getId())).withRel("shipped"));			
			break;
		case SHIPPED:
			orderModel.add(linkTo(methodOn(OrderRestController.class).completed(order.getId())).withRel("completed"));			
			break;
		case COMPLETED:
			orderModel.add(linkTo(methodOn(OrderRestController.class).archive(order.getId())).withRel("archived"));			
			break;
		case CANCELED:
			orderModel.add(linkTo(methodOn(OrderRestController.class).archive(order.getId())).withRel("archived"));			
			break;
		default:
			orderModel = null;
		}
		
		if(orderModel != null ) {
			return orderModel;
		} else {
			throw new InvalidRequestException();
		}
	}
}
