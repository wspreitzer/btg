package com.btg.website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.model.Cart;
import com.btg.website.repository.CartItemRepository;
import com.btg.website.repository.CartRepository;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.ProductRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.CartModelAssembler;

@RestController
public class CartRestController {

	@Autowired private CartRepository cartRepo;
	@Autowired private CustomerRepository customerRepo;
	@Autowired private CartItemRepository cartItemRepo;
	@Autowired private ProductRepository productRepo;
	
	private BtgSpecificationBuilder<Cart> builder;
	private CartModelAssembler assembler;
	
	@Autowired
	CartRestController(CartModelAssembler assembler) {
		this.builder = new BtgSpecificationBuilder<Cart>();
		this.assembler = assembler;
	}
	
	@GetMapping("/rest/cart/{id}")
	public EntityModel<Cart> getCartById(@PathVariable Long id) {
		return null;
	}
}
