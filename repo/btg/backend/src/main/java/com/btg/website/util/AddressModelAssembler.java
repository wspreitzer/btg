package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Controller;

import com.btg.website.controller.AddressRestController;
import com.btg.website.model.Address;

@Controller
public class AddressModelAssembler implements RepresentationModelAssembler<Address, EntityModel<Address>> {
	@Override
	public EntityModel<Address> toModel(Address address) {
		return EntityModel.of(address, 
				linkTo(methodOn(AddressRestController.class).getAddressById(address.getId())).withSelfRel(),
				linkTo(methodOn(AddressRestController.class).getAddresses()).withRel("addressess"));
	}
}