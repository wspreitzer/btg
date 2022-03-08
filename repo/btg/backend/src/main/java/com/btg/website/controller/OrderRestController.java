package com.btg.website.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.sql.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.exception.ResourceNotFoundException;
import com.btg.website.model.Order;
import com.btg.website.model.Status;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.OrderRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.BtgUtils;
import com.btg.website.util.OrderModelAssembler;
import com.btg.website.util.SearchCriteria;
import com.google.common.net.HttpHeaders;

@RestController
public class OrderRestController extends BtgRestController<Order> {

	@Autowired
	CustomerRepository customerRepo;
	
	@Autowired
	OrderRepository orderRepo;

	@Autowired
	OrderRestController(OrderModelAssembler assembler) {
		builder = new BtgSpecificationBuilder<Order>();
		this.assembler = assembler;
	}

	private final OrderModelAssembler assembler;

	@GetMapping("/rest/orders/")
	public CollectionModel<EntityModel<Order>> getCustomerOrders() {
		List<EntityModel<Order>> orders = orderRepo.findAll().stream().map(assembler::toModel).collect(toList());
		if (orders.size() > 0) {
			return CollectionModel.of(orders,
					linkTo(methodOn(OrderRestController.class).getCustomerOrders()).withSelfRel());
		} else {
			throw new ResourceNotFoundException();
		}
	}

	@GetMapping("/rest/orders/{id}")
	public EntityModel<Order> getCustomerOrdersById(@PathVariable Long id) {
		Order order = orderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", id));
		return assembler.toModel(order);
	}

	@GetMapping("/admin/rest/orders/picking")
	public EntityModel<Order> picking(@PathVariable Long id) {
		return null;
	}

	@GetMapping("/admin/rest/orders/picked")
	public EntityModel<Order> picked(@PathVariable Long id) {
		return null;
	}

	@GetMapping("/admin/rest/orders/loading")
	public EntityModel<Order> loading(@PathVariable Long id) {
		return null;
	}

	@GetMapping("/admin/rest/orders/loaded")
	public EntityModel<Order> loaded(@PathVariable Long id) {
		return null;
	}

	@GetMapping("/admin/rest/orders/shipped")
	public EntityModel<Order> shipped(@PathVariable Long id) {
		return null;
	}

	@GetMapping("/admin/rest/orders/completed")
	public EntityModel<Order> completed(@PathVariable Long id) {
		return null;
	}

	@GetMapping("/admin/rest/orders/archive")
	public EntityModel<Order> archive(@PathVariable Long id) {
		return null;
	}

	@GetMapping("/admin/rest/orders/cancel")
	public EntityModel<Order> cancel(@PathVariable Long id) {
		return null;
	}

	@GetMapping("/rest/ordersBySpecification")
	public CollectionModel<EntityModel<Order>> getCustomerOrdersBySpecification(
			@RequestParam(value = "search") String search) throws Exception {
		
		builder.with("customer", ":", 0L);
		builder = BtgUtils.buildSearchCriteria(builder, search);

		Specification<Order> spec = builder
				.build(searchCriteria -> new BtgSpecification<Order>((SearchCriteria) searchCriteria));
		List<EntityModel<Order>> ordersList = orderRepo.findAll(spec).stream().map(assembler::toModel)
				.collect(toList());
		if (ordersList.size() > 0) {
			return CollectionModel.of(ordersList,
					linkTo(methodOn(OrderRestController.class).getCustomerOrdersBySpecification(search)).withSelfRel());
		} else {
			throw new ResourceNotFoundException("Order", builder);
		}
	}

	@PostMapping("/rest/orders")
	public ResponseEntity<EntityModel<Order>> saveOrder(@RequestBody Order order, HttpServletResponse response, HttpServletRequest request) {
		order.setOrderStatus(Status.NEW);
		order.setOrderDate(new Date(System.currentTimeMillis()));
		Order newOrder = orderRepo.save(order);

		return ResponseEntity
				.created(linkTo(methodOn(OrderRestController.class).getCustomerOrdersById(newOrder.getId())).toUri())
				.header("Location", String.format("%s/btg/rest/orders/%s", request.getContextPath(), newOrder.getId(), null))
				.body(assembler.toModel(newOrder));
	}

	@PatchMapping("/rest/orders/{id}")
	public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
		Order order = orderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", id));
		Order updatedOrder = new Order();
		updatedOrder.setOrderId(order.getId());
		switch (order.getOrderStatus()) {
		case NEW:
		case PICKING:
		case PICKED:
			updatedOrder.setOrderStatus(Status.CANCELED);
			Order newOrder = orderRepo.save(updatedOrder);
			return ResponseEntity.ok(assembler.toModel(newOrder));
		default:
			StringBuffer sb = new StringBuffer();
			sb.append("You cannot cancel an order that is is in the ");
			sb.append(order.getOrderStatus());
			sb.append(" status.  Please contact customer service to arrange a return");
			return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
					.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
					.body(Problem.create().withTitle("Method NotAllowed").withDetail(sb.toString()));
		}
	}

	@PatchMapping("/rest/admin/orders/{id}/{status}")
	public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @PathVariable String status) {
		Order order = orderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", id));

		Order updatedOrder = new Order();
		updatedOrder.setOrderId(order.getId());
		Status orderStatus = Enum.valueOf(Status.class, status);
		switch (order.getOrderStatus()) {
		case NEW:
			switch (status) {
			case "PICKING":
			case "CANCELED":
				updatedOrder.setOrderStatus(orderStatus);
				return ResponseEntity.ok(assembler.toModel(orderRepo.save(order)));
			default:
				return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
						.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
						.body(Problem.create().withTitle("Method Not Allowed")
								.withDetail("New Orders may only be canceled or set to the PICKING status."));
			}
		case PICKING:
			switch (status) {
			case "PICKED":
			case "CANCELED":
				updatedOrder.setOrderStatus(orderStatus);
				return ResponseEntity.ok(assembler.toModel(orderRepo.save(order)));
			default:
				return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
						.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
						.body(Problem.create().withTitle("Method Not Allowed").withDetail(
								"Orders in the PICKING status may only be canceled or set to the PICKED status."));
			}
		case PICKED:
			switch (status) {
			case "LOADING":
			case "CANCELED":
				updatedOrder.setOrderStatus(orderStatus);
				return ResponseEntity.ok(assembler.toModel(orderRepo.save(order)));
			default:
				return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
						.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
						.body(Problem.create().withTitle("Method Not Allowed").withDetail(
								"Orders in the PICKED status may only be canceled or set to the LOADING status."));
			}
		case LOADING:
			if (status.equals("LOADED")) {
				updatedOrder.setOrderStatus(orderStatus);
				return ResponseEntity.ok(assembler.toModel(orderRepo.save(order)));

			} else {
				return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
						.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
						.body(Problem.create().withTitle("Method Not Allowed")
								.withDetail("Orders in the LOADING status may only be set to the LOADED status."));
			}
		case LOADED:
			if (status.equals("SHIPPED")) {
				updatedOrder.setOrderStatus(orderStatus);
				return ResponseEntity.ok(assembler.toModel(orderRepo.save(order)));
			} else {
				return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
						.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
						.body(Problem.create().withTitle("Method Not Allowed")
								.withDetail("Orders in the LOADED status may only be set to the SHIPPED status."));
			}
		case SHIPPED:
			if (status.equals("COMPLETED")) {
				updatedOrder.setOrderStatus(orderStatus);
				return ResponseEntity.ok(assembler.toModel(orderRepo.save(order)));
			} else {
				return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
						.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
						.body(Problem.create().withTitle("Method Not Allowed")
								.withDetail("Orders in the SHIPPED status may only be set to the COMPLETED status."));
			}
		default:
			if (status.equals("ARCHIVE")) {
				updatedOrder.setOrderStatus(orderStatus);
				orderRepo.save(updatedOrder);
				return ResponseEntity.status(HttpStatus.OK)
						.body(Problem.create().withTitle("Order Archived")
								.withDetail("Order was successfully Archived.  It will no longer show in customers history"));
			} else {
				return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
						.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
						.body(Problem.create().withTitle("Method Not Allowed")
								.withDetail("Orders in the COMPLETE or CANCELED status may only be set to the ARCHIVE status."));
			}
		}
	}
	
	@DeleteMapping("/rest/orders/{id}")
	public ResponseEntity<List<Order>> deleteCustomerOrderById(@PathVariable Long id) {
		orderRepo.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}