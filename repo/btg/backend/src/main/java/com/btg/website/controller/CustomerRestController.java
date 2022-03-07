package com.btg.website.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.sql.Date;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.exception.ResourceNotFoundException;
import com.btg.website.model.Customer;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.BtgUtils;
import com.btg.website.util.CustomerModelAssembler;
import com.btg.website.util.SearchCriteria;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

@RestController
public class CustomerRestController extends BtgRestController<Customer> {

	private final CustomerModelAssembler assembler;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	CustomerRestController(CustomerModelAssembler assembler) {
		builder = new BtgSpecificationBuilder<Customer>();
		this.assembler = assembler;
	}

	@PostMapping("/rest/customer")
	public ResponseEntity<EntityModel<Customer>> saveCustomer(@RequestBody Customer customer,
			HttpServletResponse response, HttpServletRequest request) {
		customer.setSignupDate(new Date(System.currentTimeMillis()));
		Customer newCustomer = customerRepo.save(customer);
		return ResponseEntity
				.created(linkTo(methodOn(CustomerRestController.class).getCustomerById(newCustomer.getId())).toUri())
				.header("Location",
						String.format("%s/btg/rest/customers/%s", request.getContextPath(), newCustomer.getId(), null))
				.body(assembler.toModel(newCustomer));
	}

	@PostMapping("/admin/rest/customers")
	public List<ResponseEntity<EntityModel<Customer>>> saveCustomers(@RequestBody List<Customer> customers,
			HttpServletResponse response, HttpServletRequest request) {
		Date signupDate = new Date(System.currentTimeMillis());
		customers.forEach(aCustomer -> {
			aCustomer.setSignupDate(signupDate);
		});
		List<Customer> savedCustomers = customerRepo.saveAll(customers);
		List<ResponseEntity<EntityModel<Customer>>> retVal = new ArrayList<ResponseEntity<EntityModel<Customer>>>();
		savedCustomers.forEach(c -> {
			final ResponseEntity<EntityModel<Customer>> responseEntity = ResponseEntity
					.created(linkTo(methodOn(CustomerRestController.class).getCustomerById(c.getId())).toUri())
					.header("Location",
							String.format("%s/btg/admin/rest/customers/%s", request.getContextPath(), c.getId(), null))
					.body(assembler.toModel(c));
			retVal.add(responseEntity);
		});
		return retVal;
	}

	@GetMapping("/rest/customers/{id}")
	public EntityModel<Customer> getCustomerById(@PathVariable Long id) {
		Customer customer = customerRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer", id));
		return EntityModel.of(customer,
				linkTo(methodOn(CustomerRestController.class).getCustomerById(id)).withSelfRel(),
				linkTo(methodOn(CustomerRestController.class).getCustomers()).withRel("customers"));
	}

	@GetMapping("/admin/rest/customers")
	public CollectionModel<EntityModel<Customer>> getCustomers() {
		List<EntityModel<Customer>> customers = customerRepo.findAll().stream().map(assembler::toModel)
				.collect(toList());
		if (customers.size() > 0) {
			return CollectionModel.of(customers,
					linkTo(methodOn(CustomerRestController.class).getCustomers()).withSelfRel());
		} else {
			throw new ResourceNotFoundException();
		}
	}

	@GetMapping("/rest/customerSearch")
	public CollectionModel<EntityModel<Customer>> getCustomersBySpecification(
			@RequestParam(value="search") String search) throws Exception {
		builder = BtgUtils.buildSearchCriteria(builder, search);
		Specification<Customer> spec = builder
				.build(searchCriteria -> new BtgSpecification<Customer>((SearchCriteria) searchCriteria));
		List<EntityModel<Customer>> ordersList = customerRepo.findAll(spec).stream().map(assembler::toModel)
				.collect(toList());
		if (ordersList.size() > 0) {
			return CollectionModel.of(ordersList,
					linkTo(methodOn(CustomerRestController.class).getCustomersBySpecification(search)).withSelfRel());
		} else {
			throw new ResourceNotFoundException("Order", builder);
		}
	}

	@PutMapping("/rest/customer/{id}")
	public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
		return customerRepo.findById(id).map(foundCustomer -> {
			System.out.println("this is signUpDate: " + customer.getSignupDate());
			foundCustomer.setFirstName(customer.getFirstName());
			foundCustomer.setLastName(customer.getLastName());
			foundCustomer.setBillingAddress(customer.getBillingAddress());
			foundCustomer.setShippingAddress(customer.getShippingAddress());
			foundCustomer.setCompany(customer.getCompany());
			foundCustomer.setEmail(customer.getEmail());
			foundCustomer.setPhoneNumber(customer.getPhoneNumber());
			foundCustomer.setUserName(customer.getUserName());
			foundCustomer.setPassword(customer.getPassword());
			foundCustomer.setSignupDate(customer.getSignupDate());
			foundCustomer.setWishList(customer.getWishList());
			foundCustomer.setCreditCards(customer.getCreditCards());
			Customer updatedCustomer = customerRepo.save(foundCustomer);
			return ResponseEntity.ok(assembler.toModel(updatedCustomer));
		}).orElseGet(() -> {
			customer.setId(id);
			return ResponseEntity.ok(assembler.toModel(customerRepo.save(customer)));
		});
	}

	@PatchMapping(path = "/rest/customer/{id}", consumes = "application/json-patch+json")
	public ResponseEntity<EntityModel<Customer>> updateCustomer(@PathVariable Long id, 
			@RequestBody JsonPatch patch) {
		ResponseEntity<EntityModel<Customer>> retVal;
		try {
			Customer customer = customerRepo.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Customer", id));
			Customer updatedCustomer = applyPatchToCustomer(patch, customer);
			retVal = ResponseEntity.ok(assembler.toModel(customerRepo.save(updatedCustomer)));
		} catch (JsonPatchException | JsonProcessingException e) {
			retVal = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			e.printStackTrace();
		} catch (ResourceNotFoundException e) {
			retVal = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return retVal;
	}

	@DeleteMapping("/rest/customer/{id}")
	public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
		customerRepo.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/admin/rest/customers")
	public ResponseEntity<?> deleteCustomers() {
		customerRepo.deleteAll();
		return ResponseEntity.noContent().build();
	}

	private Customer applyPatchToCustomer(JsonPatch patch, Customer targetCustomer)
			throws JsonPatchException, JsonProcessingException {
		JsonNode patched = patch.apply(objectMapper.convertValue(targetCustomer, JsonNode.class));
		return objectMapper.treeToValue(patched, Customer.class);
	}
}