package com.btg.website.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.btg.website.WebsiteApplication;
import com.btg.website.config.JacksonConfig;
import com.btg.website.model.Customer;
import com.btg.website.model.Review;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.ReviewRepository;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonConfig.class})
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK, classes= {WebsiteApplication.class})
@SuppressWarnings("unchecked")
public class ReviewRestControllerTest {

	@MockBean private ReviewRepository reviewRepo;
	@MockBean private CustomerRepository customerRepo;
	@Autowired private WebApplicationContext webApplicationContext;
	
	private MockMvc mockedRequest;
	private TestUtils<Review> reviewUtils;
	private Review review, review2, review3, review4;
	private Customer customer;
	private List<Review> reviewList; 
	private Date date;
	
	@BeforeEach
	public void setup() {
		date = new Date(System.currentTimeMillis());
		reviewUtils = new TestUtils<Review>();
		customer = new Customer(null, null, null, null, null, null, null, null, null, null);
		review = new Review(customer, "", date);
		review2 = new Review(customer, "", date);
		review3 = new Review(customer, "", date);
		review4 = new Review(customer, "", new Date(System.currentTimeMillis()));
		this.mockedRequest = webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void returns404WhenIdIsNotFound() throws Exception {
		when(reviewRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/review/0")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsAllReviews() throws Exception {
		when(reviewRepo.findAll()).thenReturn(reviewList);
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/reviews/")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsReviewWhenIdIsFound() throws Exception {
		when(reviewRepo.findById(anyLong())).thenReturn(Optional.of(review));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/review/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
}
