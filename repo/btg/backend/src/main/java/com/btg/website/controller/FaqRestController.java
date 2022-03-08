package com.btg.website.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.exception.ResourceNotFoundException;
import com.btg.website.model.Faq;
import com.btg.website.repository.FaqRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.BtgUtils;
import com.btg.website.util.FaqModelAssembler;
import com.btg.website.util.SearchCriteria;
import com.google.common.net.HttpHeaders;

@RestController
public class FaqRestController extends BtgRestController<Faq> {

	@Autowired
	FaqRepository faqRepo;

	private final FaqModelAssembler assembler;
	
	@Autowired
	FaqRestController(FaqModelAssembler assembler) {
		this.builder = new BtgSpecificationBuilder<Faq>();
		this.assembler = assembler;
	}
	
	@PostMapping("/admin/rest/faqs")
	public ResponseEntity<EntityModel<Faq>> saveFaq(@RequestBody Faq faq) {
		Faq newFaq = faqRepo.save(faq);
		return ResponseEntity
				.created(linkTo(methodOn(FaqRestController.class).getFaqById(newFaq.getId())).toUri())
				.body(assembler.toModel(newFaq));
	}
	
	@GetMapping("/rest/faqs")
	public CollectionModel<EntityModel<Faq>> getAllFaqs() {
		List<EntityModel<Faq>> faqs = faqRepo.findAll().stream().map(assembler::toModel).collect(toList());
		if(faqs.size() > 0) {
			return CollectionModel.of(faqs,
					linkTo(methodOn(FaqRestController.class).getAllFaqs()).withSelfRel());
		} else {
			throw new ResourceNotFoundException();
		}
	}

	@GetMapping("/rest/faqs/{id}")
	public EntityModel<Faq> getFaqById(@PathVariable Long id) {
		Faq faq = faqRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Faq", id));
		return assembler.toModel(faq);
	}
	
	@GetMapping("/rest/searchfaqs/")
	public CollectionModel<EntityModel<Faq>> searchFaqs(@RequestParam(value="search") String search) throws Exception {
		builder = BtgUtils.buildSearchCriteria(builder, search);
		Specification<Faq> spec = builder
				.build(searchCriteria -> new BtgSpecification<Faq>((SearchCriteria) searchCriteria));
		List<EntityModel<Faq>> faqList = faqRepo.findAll(spec).stream().map(assembler::toModel)
				.collect(toList());
		if(faqList.size() > 0) {
			return CollectionModel.of(faqList, 
					linkTo(methodOn(FaqRestController.class).searchFaqs(search)).withSelfRel());
		} else {
			throw new ResourceNotFoundException("Faq", builder);
		}
	}
	
	@PatchMapping("/admin/rest/faqs/{id}")
	public ResponseEntity<?> updateFaqByField(@PathVariable Long id, 
			@RequestParam(value = "field") String field, 
			@RequestParam(value = "update") String update) {
		ResponseEntity<?> retVal;
		Faq updatedFaq = new Faq();
		updatedFaq.setId(id);
		switch(field) {
		case "question":
			updatedFaq.setQuestion(update);
			break;
		case "answer":
			updatedFaq.setAnswer(update);
			break;
		default:
			retVal = ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
					.body(Problem.create()
							.withTitle("Bad Request").withDetail("Invalid field name provided.  Please provide a valid field name"));
		}
		retVal = ResponseEntity.ok(assembler.toModel(faqRepo.save(updatedFaq)));
		return retVal;
	}
	
	@PutMapping("/admin/rest/faqs/")
	public ResponseEntity<?> updateFaq(@RequestBody Faq updatedFaq) {
		Faq newFaq = faqRepo.save(updatedFaq);
		return ResponseEntity
				.created(linkTo(methodOn(FaqRestController.class).getFaqById(newFaq.getId())).toUri())
				.body(assembler.toModel(newFaq));
	}
	
	@DeleteMapping("/admin/rest/faqs/{id}")
	public ResponseEntity<List<Faq>> deleteFaqById(@PathVariable Long id) {
		faqRepo.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}