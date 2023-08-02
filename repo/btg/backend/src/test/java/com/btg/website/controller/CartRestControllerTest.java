package com.btg.website.controller;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.btg.website.model.Cart;
import com.btg.website.model.CartItem;
import com.btg.website.model.Customer;
import com.btg.website.model.Product;
import com.btg.website.repository.CartItemRepository;
import com.btg.website.repository.CartRepository;
import com.btg.website.repository.ProductRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.util.CartModelAssembler;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CartRestController.class)
@SuppressWarnings("unchecked")
public class CartRestControllerTest {
	
	@MockBean private CartRepository cartRepo;
	@MockBean private CartItemRepository cartItemRepo;
	@MockBean private ProductRepository productRepo;
	@MockBean private CartModelAssembler assembler;
	@MockBean private BtgSpecificationBuilder<Cart> builder;
	@Autowired private MockMvc mockedRequest;
	
	
	private Cart cart, cart2, cart3, cart4;
	private Customer customer, customer2, customer3, customer4;
	private CartItem cartItem, cartItem2, cartItem3, cartItem4;
	private TestUtils<Cart> cartUtils;
	
	@BeforeEach
	public void setup() {
		when(productRepo.findById(10L)).thenReturn(Optional.of(new Product("A new product", "This is a sku", "This is a product", 10, 15.95)));
		Product product = productRepo.findById(10L).get();
		when(productRepo.findById(11L)).thenReturn(Optional.of(new Product("A new product2", "This is a sku2", "This is a product3", 10, 16.95)));
		Product product2 = productRepo.findById(11L).get();
		when(productRepo.findById(12L)).thenReturn(Optional.of(new Product("A new product3", "This is a sku3", "This is a product3", 10, 17.95)));
		Product product3 = productRepo.findById(12L).get();
		when(productRepo.findById(13L)).thenReturn(Optional.of(new Product("A new product4", "This is a sku4", "This is a product4", 10, 18.95)));
		Product product4 = productRepo.findById(13L).get();
		
		when(cartItemRepo.findById(1L)).thenReturn(Optional.of(new CartItem(product, 0, 0)));
		when(cartItemRepo.findById(2L)).thenReturn(Optional.of(new CartItem(product2, 0, 0)));
		when(cartItemRepo.findById(3L)).thenReturn(Optional.of(new CartItem(product3, 0, 0)));
		when(cartItemRepo.findById(4L)).thenReturn(Optional.of(new CartItem(product4, 0, 0)));
		when(assembler.toModel(any(Cart.class))).thenCallRealMethod();
	}
}