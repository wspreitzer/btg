package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import com.btg.website.controller.CartRestController;
import com.btg.website.model.Cart;

public class CartModelAssembler implements RepresentationModelAssembler<Cart, EntityModel<Cart>> {

	@Override
	public EntityModel<Cart> toModel(Cart cart) {
		return EntityModel.of(cart, 
				linkTo(methodOn(CartRestController.class).getCartById(cart.getId())).withSelfRel());
	}
}
