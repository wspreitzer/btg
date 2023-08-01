package com.btg.website.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.btg.website.WebsiteApplication;
import com.btg.website.model.Customer;
import com.btg.website.model.Product;
import com.btg.website.model.WishList;
import com.btg.website.repository.WishListRepository;
import com.btg.website.util.TestUtils;
import com.btg.website.util.WishListModelAssembler;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WishListRestController.class)
@SuppressWarnings("unchecked")
public class WishListRestControllerTest {
	
	@MockBean WishListRepository wishListRepo;
	@MockBean WishListModelAssembler assembler;
	@Autowired
	private MockMvc mockedRequest;
	
	private TestUtils<WishList> wishListUtils;
	private WishList wishList;
	private Customer customer;
	private Product product, product2;
	private List<Product> productList;
	private List<WishList> wishListList;
	
	@BeforeEach
	public void setup() {
		customer = new Customer("Bob", "Smith", "bob.smith@comcast.net", 
				"222-805-2222", "user1", "p@ssword");
		customer.setSignupDate(new Date(System.currentTimeMillis()));
		product = new Product("New Product", "New sku 1", "This is a new product", 100, 21.95);
		product2 = new Product("New Product 2", "New sku 2", "This is a new product2", 100, 23.95);
		productList = new ArrayList<Product>();
		productList.add(product);
		productList.add(product2);
		wishList = new WishList(customer, productList, new Date(System.currentTimeMillis()));
		wishListUtils = new TestUtils<WishList>();
		wishListList = wishListUtils.setupRepository(wishList);
	}
	
	@Test
	public void createsWishListWhenRequestIsValid() throws Exception {
		WishList wishListToSave = new WishList(customer, productList, new Date(System.currentTimeMillis()));
		wishListToSave.setId(69L);
		when(wishListRepo.save(any(WishList.class))).thenReturn(wishListToSave);
		MvcResult mvcResult = mockedRequest
				.perform(post("/btg/rest/wishList")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id\" : \"69\", \"customer\" : { \"firstName\" : \"Bob\", \"lastName\" : \"Smith\", \"email\" : \"bob.smith@comcast.net\", \"phoneNumber\" : \"222-805-2222\", \"userName\" : \"user1\", \"password\" : \"p@ssword\"}, \"products\" : [{\"name\" : \"New Product\", \"sku\" : \"New sku 1\", \"description\" : \"This is a new product 1\", \"qty\":\"100\", \"price\" : \"21.95\"}, {\"name\" : \"New Product2\", \"sku\" : \"New sku2\", \"description\" : \"This is a new product 2\", \"qty\" : \"100\", \"price\" : \"22.95\"}], \"addedDate\" : \"" + new Date(System.currentTimeMillis()) + " \"}]")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.customer.firstName").value("Bob"))
				.andExpect(jsonPath("$.customer.lastName").value("Smith"))
				.andExpect(jsonPath("$.customer.email").value("bob.smith@comcast.net"))
				.andExpect(jsonPath("$.customer.phoneNumber").value("222-805-2222"))
				.andExpect(jsonPath("$.customer.userName").value("user1"))
				.andExpect(jsonPath("$.customer.password").value("p@ssword")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returns404WhenIdIsNotFound() throws Exception {
		when(wishListRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/wishList/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsWishListwhenIdIsFound() throws Exception {
		when(wishListRepo.findById(anyLong())).thenReturn(Optional.of(wishList));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/wishList/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesWishListProducts() throws Exception {
		when(wishListRepo.findById(anyLong())).thenReturn(Optional.of(wishList));
		when(wishListRepo.save(any(WishList.class))).thenReturn(wishList);
		Product updatedProduct = new Product("New Prod", "GHS-3r234", "A new product", 10, 25.95);
		productList.add(updatedProduct);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/updateWishList/1")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"add\", \"path\" : \"/products/0\", \"value\" : {\"name\" : \"New Prod\", \"sku\" : \"GHS-3r234\", \"description\" : \"A new product\", \"qty\" : \"10\", \"price\" : \"25.95\"} }]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		wishList.setProducts(productList);
		WishList foundWishList = wishListRepo.findById(1L).get();
		assertThat(foundWishList.getProducts().size(), is(3));
		assertThat(foundWishList.getProducts().get(2).getName(), is("New Prod"));
		assertThat(foundWishList.getProducts().get(2).getSku(), is("GHS-3r234"));
		assertThat(foundWishList.getProducts().get(2).getDescription(), is("A new product"));
		assertThat(foundWishList.getProducts().get(2).getQty(), is(10));
		assertThat(foundWishList.getProducts().get(2).getPrice(), is(25.95));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void deletesWishListWhenRequestIsValid() throws Exception {
		when(wishListRepo.findById(anyLong())).thenReturn(Optional.of(wishList));
		WishList foundWishList = wishListRepo.findById(1L).get();
		doAnswer(invocation -> {
			wishListList.remove(0);
			return null;
		}).when(wishListRepo).deleteById(anyLong());
		mockedRequest.perform(delete("/btg/rest/wishList/1")).andExpect(status().isNoContent());
		verify(wishListRepo).deleteById(1L);
		assertThat(wishListList.size(), is(0));
		assertThat(wishListList, not(hasItem(foundWishList)));
	}	
}