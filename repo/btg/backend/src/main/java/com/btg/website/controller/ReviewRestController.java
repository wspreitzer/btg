package com.btg.website.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.exception.ResourceNotFoundException;
import com.btg.website.model.Review;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.ReviewRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.BtgUtils;
import com.btg.website.util.ReviewModelAssembler;
import com.btg.website.util.SearchCriteria;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

@RestController
public class ReviewRestController extends BtgRestController<Review>{

	@Autowired private ReviewRepository reviewRepo;
	@Autowired private CustomerRepository customerRepo;
	
	private final ReviewModelAssembler assembler;
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	ReviewRestController(ReviewModelAssembler assembler) {
		builder = new BtgSpecificationBuilder<Review>();
		this.assembler = assembler;
	}
	
	@PostMapping("/rest/review")
	ResponseEntity<EntityModel<Review>> createReview(@RequestBody Review review, HttpServletResponse response, HttpServletRequest request) {
		Review newReview = reviewRepo.save(review);
		return ResponseEntity
			.created(linkTo(methodOn(ReviewRestController.class)
					.getReviewById(newReview.getId())).toUri())
				.header("Location", String.format
						("%s/btg/rest/review/%s", 
								request.getContextPath(), 
								newReview.getId(), null))
				.body(assembler.toModel(newReview));
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
	
	@GetMapping("/rest/searchReviews")
	public CollectionModel<EntityModel<Review>> searchReviews(@RequestParam(value ="search") String search) throws Exception {
		builder = BtgUtils.buildSearchCriteria(builder, search);
		Specification<Review> spec = builder.build(searchCriteria -> new BtgSpecification<Review>((SearchCriteria) searchCriteria));
		List<EntityModel<Review>> reviewList = reviewRepo.findAll(spec)
				.stream()
				.map(assembler::toModel)
				.collect(toList());
		if(reviewList.size() > 0) {
			return CollectionModel.of(reviewList,
					linkTo(methodOn(ReviewRestController.class).searchReviews(search)).withSelfRel());
		} else {
			throw new ResourceNotFoundException("Review", builder);
		}
	}
	
	@PatchMapping(path = "/rest/review/{id}", consumes = "application/json-patch+json")
	public ResponseEntity<EntityModel<Review>> updateReview(@PathVariable Long id, @RequestBody JsonPatch patch) {
		ResponseEntity<EntityModel<Review>> retVal;
		try {
			Review review = reviewRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review", id));
			Review updatedReview = applyPatchToReview(patch, review);
			retVal = ResponseEntity.ok(assembler.toModel(reviewRepo.save(updatedReview)));
		} catch (JsonPatchException | JsonProcessingException e) {
			retVal = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			e.printStackTrace();
		} catch (ResourceNotFoundException e) {
			retVal = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return retVal;
	}
	
	@DeleteMapping("/rest/review/{id}")
	public ResponseEntity<?> deleteReview(@PathVariable Long id) {
		reviewRepo.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	private Review applyPatchToReview(JsonPatch patch, Review targetReview) throws JsonPatchException, JsonProcessingException {
		JsonNode patched = patch.apply(objectMapper.convertValue(targetReview, JsonNode.class));
		return objectMapper.treeToValue(patched, Review.class);
	}
}