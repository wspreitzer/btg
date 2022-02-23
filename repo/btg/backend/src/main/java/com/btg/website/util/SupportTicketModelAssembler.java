package com.btg.website.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.btg.website.controller.SupportTicketRestController;
import com.btg.website.model.SupportTicket;

@Component
public class SupportTicketModelAssembler implements RepresentationModelAssembler<SupportTicket, EntityModel<SupportTicket>> {

	@Override
	public EntityModel<SupportTicket> toModel(SupportTicket supportTicket) {
		return EntityModel.of(supportTicket,
				linkTo(methodOn(SupportTicketRestController.class).getSupportTicketById(supportTicket.getId())).withSelfRel(),
				linkTo(methodOn(SupportTicketRestController.class).getAllCustomerTickets()).withRel("supportTickets"));
	}
}