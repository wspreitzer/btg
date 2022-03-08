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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.exception.ResourceNotFoundException;
import com.btg.website.model.Address;
import com.btg.website.repository.AddressRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.AddressModelAssembler;
import com.btg.website.util.BtgUtils;
import com.btg.website.util.SearchCriteria;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

@RestController
public class AddressRestController extends BtgRestController<Address> {

	@Autowired private AddressRepository addressRepo;
	@Autowired private BtgSpecificationBuilder<Address> builder;
	
	private final AddressModelAssembler assembler;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	AddressRestController(AddressModelAssembler assembler) {
		this.assembler = assembler;
	}
	
	@PostMapping("/rest/address")
	public ResponseEntity<EntityModel<Address>> createAddress(@RequestBody Address address, HttpServletResponse response, HttpServletRequest request) {
		Address newAddress = addressRepo.save(address);
		return ResponseEntity
				.created(linkTo(methodOn(AddressRestController.class)
						.getAddressById(newAddress.getId())).toUri())
				.header("Location", String.format("%s/btg/rest/address/%s", request.getContextPath(), newAddress.getId(), null))
				.body(assembler.toModel(newAddress));
	}
	
	@GetMapping("/admin/rest/addresses")
	public CollectionModel<EntityModel<Address>> getAddresses() {
		List<EntityModel<Address>> addresses = addressRepo
				.findAll()
				.stream()
				.map(assembler::toModel)
				.collect(toList());
		if(addresses.size() > 0) {
			return CollectionModel.of(addresses, 
					linkTo(methodOn(AddressRestController.class).getAddresses()).withSelfRel());
		} else {
			throw new ResourceNotFoundException();
		}
	}
	
	@GetMapping("/rest/address/{id}")
	public EntityModel<Address> getAddressById(@PathVariable Long id) {
		Address address = addressRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address", id));
		return assembler.toModel(address);
	}
	
	@GetMapping("/rest/searchAddresses")
	public CollectionModel<EntityModel<Address>> searchAddresses(@RequestParam(value ="search") String search) {
		builder = BtgUtils.buildSearchCriteria(builder, search);
		Specification<Address> spec = builder.build(searchCriteria -> new BtgSpecification<Address>((SearchCriteria) searchCriteria));
		List<EntityModel<Address>> addressList = addressRepo.findAll(spec)
				.stream()
				.map(assembler::toModel)
				.collect(toList());
		if(addressList.size() > 0) {
			return CollectionModel.of(addressList,
					linkTo(methodOn(AddressRestController.class).searchAddresses(search)).withSelfRel());
		} else {
			throw new ResourceNotFoundException("Address", builder);
		}
	}
	
	@PatchMapping(path = "/rest/address/{id}", consumes = "application/json-patch+json")
	public ResponseEntity<EntityModel<Address>> updateAddress(@PathVariable Long id, @RequestBody JsonPatch patch) {
		ResponseEntity<EntityModel<Address>> retVal;
		try {
			Address address = addressRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address", id));
			Address updatedAddress = applyPatchToAddress(patch, address);
			retVal = ResponseEntity.ok(assembler.toModel(addressRepo.save(updatedAddress)));
		} catch (JsonPatchException | JsonProcessingException e) {
			retVal = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			e.printStackTrace();
		} catch (ResourceNotFoundException e) {
			retVal = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return retVal;
	}
	
	@PutMapping("/rest/address/{id}")
	public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody Address address) {
		return addressRepo.findById(id)
				.map(foundAddress -> {
					foundAddress.setStreet(address.getStreet());
					foundAddress.setCity(address.getCity());
					foundAddress.setState(address.getState());
					foundAddress.setZipCode(address.getZipCode());
					Address updatedAddress = addressRepo.save(foundAddress);
					return ResponseEntity.ok(assembler.toModel(updatedAddress));
				}).orElseGet(() -> {
					address.setId(id);
					return ResponseEntity.ok(assembler.toModel(addressRepo.save(address)));
				});
	}
	
	@DeleteMapping("/rest/address/{id}")
	public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
		addressRepo.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	private Address applyPatchToAddress(JsonPatch patch, Address targetAddress) throws JsonPatchException, JsonProcessingException {
		JsonNode patched = patch.apply(objectMapper.convertValue(targetAddress, JsonNode.class));
		return objectMapper.treeToValue(patched, Address.class);
	}
}