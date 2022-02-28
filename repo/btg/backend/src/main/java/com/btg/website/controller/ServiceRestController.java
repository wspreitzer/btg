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
import com.btg.website.model.Service;
import com.btg.website.repository.ServiceRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.BtgUtils;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.ServiceModelAssembler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

@RestController
public class ServiceRestController extends BtgRestController<Service>{

	@Autowired private ServiceRepository serviceRepo;
	private final ServiceModelAssembler assembler;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	ServiceRestController(ServiceModelAssembler assembler) {
		builder = new BtgSpecificationBuilder<Service>();
		this.assembler = assembler;
	}
	
	@PostMapping("/admin/rest/service")
	public ResponseEntity<EntityModel<Service>> createService(@RequestBody Service service, HttpServletResponse response, HttpServletRequest request) {
		Service newService = serviceRepo.save(service);
		return ResponseEntity
				.created(linkTo(methodOn(ServiceRestController.class).getServiceById(newService.getId())).toUri())
				.header("Location", String.format("%s/btg/admin/rest/service/%s", request.getContextPath(), newService.getId(), null))
				.body(assembler.toModel(newService));
	}
	
	@GetMapping("/rest/services/")
	public CollectionModel<EntityModel<Service>> getServices() {
		List<EntityModel<Service>> services = serviceRepo.findAll().stream().map(assembler :: toModel).collect(toList());
		if(services.size() > 0) {
			return CollectionModel.of(services,
					linkTo(methodOn(ServiceRestController.class).getServices()).withSelfRel());
		} else {
			throw new ResourceNotFoundException();
		}
	}
	
	@GetMapping("/rest/service/{id}")
	public EntityModel<Service> getServiceById(@PathVariable Long id) {
		Service service = serviceRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Service", id));
		return assembler.toModel(service);
	}
	
	@GetMapping("/rest/searchServices")
	public CollectionModel<EntityModel<Service>> getServiceBySpecification(@RequestParam(value="search") String search) throws Exception {
		builder = BtgUtils.buildSearchCriteria(builder, search);
		Specification<Service> spec = builder.build(searchCriteria -> new BtgSpecification<Service>((SearchCriteria) searchCriteria));
		List<EntityModel<Service>> serviceList = serviceRepo.findAll(spec).stream().map(assembler::toModel).collect(toList());
		if(serviceList.size() > 0) {
			return CollectionModel.of(serviceList,
					linkTo(methodOn(ServiceRestController.class).getServiceBySpecification(search)).withSelfRel());
		} else {
			throw new ResourceNotFoundException("Service", builder);
		}
	}
	
	@PutMapping("/admin/rest/updateService/{id}")
	public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody Service service) {
		return serviceRepo.findById(id).map(foundService -> {
			foundService.setServiceName(service.getServiceName());
			foundService.setDescription(service.getDescription());
			foundService.setPrice(service.getPrice());
			Service updatedService = serviceRepo.save(foundService);
			return ResponseEntity.ok(assembler.toModel(updatedService));
		}).orElseGet(() -> {
			service.setId(id);
			return ResponseEntity.ok(assembler.toModel(serviceRepo.save(service)));
		});
	}
	
	@PatchMapping(path = "/admin/rest/service/{id}", consumes = "application/json-patch+json")
	public ResponseEntity<EntityModel<Service>> updateServiceMember(@PathVariable Long id, @RequestBody JsonPatch patch) {
		ResponseEntity<EntityModel<Service>> retVal;
		try {
			Service service = serviceRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Service", id));
			Service updatedService = applyPatchToService(patch, service);
			retVal = ResponseEntity.ok(assembler.toModel(serviceRepo.save(updatedService)));
		} catch (JsonPatchException | JsonProcessingException e) {
			e.printStackTrace();
			retVal = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} catch (ResourceNotFoundException e) {
			retVal = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return retVal;
	}
	
	@DeleteMapping("/admin/rest/services")
	public ResponseEntity<?> deleteAllServices() {
		serviceRepo.deleteAll();
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/admin/rest/service/{id}")
	public ResponseEntity<?> deleteServiceById(@PathVariable Long id) {
		serviceRepo.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	private Service applyPatchToService(JsonPatch patch, Service targetService) throws JsonPatchException, JsonProcessingException {
		JsonNode patched = patch.apply(objectMapper.convertValue(targetService, JsonNode.class));
		return objectMapper.treeToValue(patched, Service.class);
	}
}