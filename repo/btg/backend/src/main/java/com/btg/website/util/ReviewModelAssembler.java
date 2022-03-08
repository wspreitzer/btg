package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.btg.website.controller.ReviewRestController;
import com.btg.website.model.Review;

@Component
public class ReviewModelAssembler implements RepresentationModelAssembler<Review, EntityModel<Review>> {
	@Override
	public EntityModel<Review> toModel(Review review) {
		return EntityModel.of(review, 
				linkTo(methodOn(ReviewRestController.class).getReviewById(review.getId())).withSelfRel(),
				linkTo(methodOn(ReviewRestController.class).getReviews()).withRel("reviews"));
	}
}