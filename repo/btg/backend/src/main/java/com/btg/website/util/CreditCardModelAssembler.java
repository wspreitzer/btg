package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.btg.website.controller.CreditCardRestController;
import com.btg.website.model.CreditCard;

@Component
public class CreditCardModelAssembler implements RepresentationModelAssembler<CreditCard, EntityModel<CreditCard>> {
	
	@Override
	public EntityModel<CreditCard> toModel(CreditCard card) {
		return EntityModel.of(card, 
				linkTo(methodOn(CreditCardRestController.class).getCreditCardById(card.getId())).withSelfRel(),
				linkTo(methodOn(CreditCardRestController.class).getUserCreditCards()).withRel("creditCards"));
	}
}