package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.btg.website.controller.ServiceRestController;
import com.btg.website.model.Service;

@Component
public class ServiceModelAssembler implements RepresentationModelAssembler<Service, EntityModel<Service>> {
	@Override
	public EntityModel<Service> toModel(Service service) {
		return EntityModel.of(service, 
				linkTo(methodOn(ServiceRestController.class).getServiceById(service.getId())).withSelfRel(),
				linkTo(methodOn(ServiceRestController.class).getServices()).withRel("services"));
	}
}
