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

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.btg.website.model.Customer;
import com.btg.website.model.Review;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.ReviewRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.util.ReviewModelAssembler;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReviewRestController.class)
@SuppressWarnings("unchecked")
public class ReviewRestControllerTest {

	@MockBean private ReviewRepository reviewRepo;
	@MockBean private ReviewModelAssembler assembler;
	@MockBean private CustomerRepository customerRepo;
	@MockBean private BtgSpecificationBuilder<Review> builder;
	@Autowired private MockMvc mockedRequest;
	
	private TestUtils<Review> reviewUtils;
	private Review review, review2, review3, review4;
	private Customer customer;
	private List<Review> reviewList; 
	private Date date;
	
	@BeforeEach
	public void setup() {
		date = new Date(System.currentTimeMillis());
		reviewUtils = new TestUtils<Review>();
		customer = new Customer("Mason", "Daniels", "mason.daniels@email.com", 
				"224-123-3656", "mdaniels", "P@ssw0rd");
		review = new Review(customer, "This Place is Awesome", date);
		review2 = new Review(customer, "This Joint is Good", date);
		review3 = new Review(customer, "This Joint is Awesome", date);
		review4 = new Review(customer, "This Place is Bad", new Date(System.currentTimeMillis()));
		reviewList = reviewUtils.setupRepository(review, review2, review3, review4);
		when(assembler.toModel(any(Review.class))).thenCallRealMethod();
	}
	
	@Test
	public void createsReviewWhenRequestIsValid() throws Exception {
		Review reviewToSave = new Review(customer, "Very good place to do business with", date);
		when(reviewRepo.save(any(Review.class))).thenReturn(reviewToSave);
		MvcResult mvcResult = mockedRequest
				.perform(post("/btg/rest/review")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"customer\" : { \"firstName\" : \"Mason\", \"lastName\" : \"Daniels\", \"email\" : \"mason.daniels@email.com\", \"phoneNumber\" : \"224-123-3656\", \"userName\" : \"mdaniels\", \"password\" : \"P@ssw0rd\"}, \"review\" : \"Very good place to do business with\", \"reviewDate\" : \"" + date + "\"}")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.customer.firstName").value("Mason"))
				.andExpect(jsonPath("$.customer.lastName").value("Daniels"))
				.andExpect(jsonPath("$.customer.email").value("mason.daniels@email.com"))
				.andExpect(jsonPath("$.customer.phoneNumber").value("224-123-3656"))
				.andExpect(jsonPath("$.customer.username").value("mdaniels"))
				.andExpect(jsonPath("$.customer.password").value("P@ssw0rd"))
				.andExpect(jsonPath("$.review").value("Very good place to do business with")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returns404WhenIdIsNotFound() throws Exception {
		when(reviewRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/review/0")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
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
	
	@Test
	public void returnsReviewWhenReviewEquals() throws Exception {
		when(reviewRepo.findAll(any(Specification.class))).thenReturn(reviewUtils.setupRepository(review));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchReviews")
						.param("search", "review:This Place is Awesome")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsReviewWhenReviewBeginsWith() throws Exception {
		when(reviewRepo.findAll(any(Specification.class))).thenReturn(reviewUtils.setupRepository(review, review2, review3, review4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchReviews")
						.param("search", "review:This*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsReviewWhenReviewEndsWith() throws Exception {
		when(reviewRepo.findAll(any(Specification.class))).thenReturn(reviewUtils.setupRepository(review, review3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchReviews")
						.param("search", "review:*Awesome")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsReviewWhenReviewContains() throws Exception {
		when(reviewRepo.findAll(any(Specification.class))).thenReturn(reviewUtils.setupRepository(review2, review3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchReviews")
						.param("search", "review:*Joint*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsReviewWhenReviewDoesNotEqual() throws Exception {
		when(reviewRepo.findAll(any(Specification.class))).thenReturn(reviewUtils.setupRepository(review, review2, review3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchReviews")
						.param("search", "review!This Place is Bad")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updateReviewReviewWhenRequestIsValid() throws Exception {
		when(reviewRepo.findById(anyLong())).thenReturn(Optional.of(review));
		when(reviewRepo.save(any(Review.class))).thenReturn(review);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/review/1")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/review\", \"value\" : \"This is the best website designer in the world!!!\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		review.setReview("This is the best website designer in the world!!!");
		Review foundReview = reviewRepo.findById(1L).get();
		assertThat(foundReview.getReview(), is("This is the best website designer in the world!!!"));
		assertThat(foundReview.getReviewDate(), is(review.getReviewDate()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updateReviewReviewDateWhenRequestIsValid() throws Exception {
		when(reviewRepo.findById(anyLong())).thenReturn(Optional.of(review2));
		when(reviewRepo.save(any(Review.class))).thenReturn(review2);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/review/2")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/reviewDate\", \"value\" : \"86400000\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		review2.setReviewDate(new Date(86400000L));
		Review foundReview = reviewRepo.findById(2L).get();
		assertThat(foundReview.getReview(), is(review2.getReview()));
		assertThat(foundReview.getReviewDate().toString(), is(Date.valueOf("1970-01-01").toString()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updateReviewAndReviewDateWhenRequestIsValid() throws Exception {
		when(reviewRepo.findById(anyLong())).thenReturn(Optional.of(review3));
		when(reviewRepo.save(any(Review.class))).thenReturn(review3);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/review/3")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/review\", \"value\" : \"This is the best website designer in the world!!!\"},{\"op\" : \"replace\", \"path\" : \"/reviewDate\", \"value\" : \"86400000\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		review3.setReview("This is the best website designer in the world!!!");
		review3.setReviewDate(new Date(86400000L));
		Review foundReview = reviewRepo.findById(3L).get();
		assertThat(foundReview.getReview(), is("This is the best website designer in the world!!!"));
		assertThat(foundReview.getReviewDate().toString(), is(Date.valueOf("1970-01-01").toString()));
		System.out.println(mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void deleteReviewByIdWhenRequestIsValid() throws Exception {
		when(reviewRepo.findById(anyLong())).thenReturn(Optional.of(review4));
		Review foundReview = reviewRepo.findById(4L).get();
		doAnswer(invocation -> {
			reviewList.remove(3);
			return null;
		}).when(reviewRepo).deleteById(anyLong());
		mockedRequest.perform(delete("/btg/rest/review/4")).andExpect(status().isNoContent());
		verify(reviewRepo).deleteById(4L);
		assertThat(reviewList.size(), is(3));
		assertThat(reviewList, not(hasItem(foundReview)));
	}
	
}