package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.btg.website.controller.WishListRestController;
import com.btg.website.model.WishList;

@Component
public class WishListModelAssembler implements RepresentationModelAssembler<WishList, EntityModel<WishList>> {

	@Override
	public EntityModel<WishList> toModel(WishList wishList) {
		return EntityModel.of(wishList, 
				linkTo(methodOn(WishListRestController.class).getWishListById(wishList.getId())).withSelfRel());
	}	
}