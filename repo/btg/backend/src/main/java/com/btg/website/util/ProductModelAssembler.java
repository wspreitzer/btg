package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.btg.website.controller.ProductRestController;
import com.btg.website.model.Product;

@Component
public class ProductModelAssembler implements RepresentationModelAssembler<Product, EntityModel<Product>> {

	@Override
	public EntityModel<Product> toModel(Product product) {
		return EntityModel.of(product, 
				linkTo(methodOn(ProductRestController.class).getProductById(product.getId())).withSelfRel(),
				linkTo(methodOn(ProductRestController.class).getProducts()).withRel("products"));
	}
}
