package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.btg.website.controller.FaqRestController;
import com.btg.website.model.Faq;

@Component
public class FaqModelAssembler implements RepresentationModelAssembler<Faq, EntityModel<Faq>>{
	
	@Override
	public EntityModel<Faq> toModel(Faq faq) {
		return EntityModel.of(faq,
				linkTo(methodOn(FaqRestController.class).getFaqById(faq.getId())).withSelfRel(),
				linkTo(methodOn(FaqRestController.class).getAllFaqs()).withRel("faqs"));
	}
}
