package com.btg.website.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.exception.ResourceNotFoundException;
import com.btg.website.model.Review;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.ReviewRepository;
import com.btg.website.util.ReviewModelAssembler;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ReviewRestController extends BtgRestController<Review>{

	@Autowired private ReviewRepository reviewRepo;
	@Autowired private CustomerRepository customerRepo;
	
	private final ReviewModelAssembler assembler;
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	ReviewRestController(ReviewModelAssembler assembler) {
		this.assembler = assembler;
	}
	
	@GetMapping("/rest/reviews")
	public CollectionModel<EntityModel<Review>> getReviews() {
		List<EntityModel<Review>> reviews = reviewRepo.findAll()
				.stream().map(assembler::toModel).collect(toList());
		if (reviews.size() > 0) {
			return CollectionModel.of(reviews,
					linkTo(methodOn(ReviewRestController.class)
							.getReviews()).withSelfRel());
		} else {
			throw new ResourceNotFoundException();
		}
	}
	
	@GetMapping("/rest/review/{id}")
	public EntityModel<Review> getReviewById(@PathVariable Long id) {
		Review review = reviewRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review", id));
		return assembler.toModel(review);
	}
	
	
}