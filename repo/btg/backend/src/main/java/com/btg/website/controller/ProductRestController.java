																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																	package com.btg.website.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
import com.btg.website.model.Product;
import com.btg.website.repository.ProductRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.BtgUtils;
import com.btg.website.util.ProductModelAssembler;
import com.btg.website.util.SearchCriteria;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

@RestController
public class ProductRestController extends BtgRestController<Product> {

	@Autowired private ProductRepository productRepo;
	private final ProductModelAssembler assembler;

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	ProductRestController(ProductModelAssembler assembler) {
		this.builder = new BtgSpecificationBuilder<Product>();
		this.assembler = assembler;
	}
	
	@PostMapping("/admin/rest/product")
	public ResponseEntity<EntityModel<Product>> createProduct(@RequestBody Product product, HttpServletResponse response, HttpServletRequest request) {
		Product newProduct = productRepo.save(product);
		return ResponseEntity
				.created(linkTo(methodOn(ProductRestController.class).getProductById(newProduct.getId())).toUri())
				.header("Location", String.format("%s/btg/admin/product/%s", request.getContextPath(), newProduct.getId(), null))
				.body(assembler.toModel(newProduct));
	}
	
	@PostMapping("/admin/rest/products")
	public List<ResponseEntity<EntityModel<Product>>> createProducts(@RequestBody List<Product> products, HttpServletResponse response, HttpServletRequest request) {
		List<Product> savedProducts = productRepo.saveAll(products);
		List<ResponseEntity<EntityModel<Product>>> retVal = new ArrayList<ResponseEntity<EntityModel<Product>>>();
		savedProducts.forEach(c -> {
			final ResponseEntity<EntityModel<Product>> responseEntity = ResponseEntity
					.created(linkTo(methodOn(ProductRestController.class).getProductById(c.getId())).toUri())
					.header("Location", String.format("%s/btg/admin/rest/products/%s", request.getContextPath(), c.getId(), null))
					.body(assembler.toModel(c));
			retVal.add(responseEntity);
		});
		return retVal;
	}
	
	@GetMapping("/rest/products/")
	public CollectionModel<EntityModel<Product>> getProducts() {
		List<EntityModel<Product>> products = productRepo.findAll()
				.stream()
				.map(assembler::toModel).collect(toList());
		if(products.size() > 0) {
			return CollectionModel.of(products,
					linkTo(methodOn(ProductRestController.class).getProducts()).withSelfRel());
		} else {
			throw new ResourceNotFoundException();
		}
	}
	
	@GetMapping("/rest/product/{id}")
	public EntityModel<Product> getProductById(@PathVariable Long id) {
		Product product = productRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
		return EntityModel.of(product, 
				linkTo(methodOn(ProductRestController.class).getProductById(id)).withSelfRel(),
				linkTo(methodOn(ProductRestController.class).getProducts()).withRel("products"));
	}
	
	@GetMapping("/rest/productSearch")
	public CollectionModel<EntityModel<Product>> getProductsBySpecification(@RequestParam(value = "search") String search) throws Exception {
		this.builder = BtgUtils.buildSearchCriteria(builder, search);
		Specification<Product> spec = builder
				.build(searchCriteria -> new BtgSpecification<Product>((SearchCriteria) searchCriteria));
		List<EntityModel<Product>> productList = productRepo.findAll(spec).stream().map(assembler::toModel).collect(toList());
		if(productList.size() > 0) {
			return CollectionModel.of(productList,
					linkTo(methodOn(ProductRestController.class).getProductsBySpecification(search)).withSelfRel());
		} else {
			throw new ResourceNotFoundException("Product", builder);
		}
	}
	
	@PutMapping("/admin/rest/updateProductField/{id}")
	public ResponseEntity<?> updfdateProduct(@PathVariable Long id, @RequestBody Product product) {
		return productRepo.findById(id).map(foundProduct -> {
					foundProduct.setName(product.getName());
					foundProduct.setSku(product.getSku());
					foundProduct.setDescription(product.getDescription());
					foundProduct.setQty(product.getQty());
					foundProduct.setPrice(product.getPrice());
					Product updatedProduct = productRepo.save(foundProduct);
					return ResponseEntity.ok(assembler.toModel(updatedProduct));
				}).orElseGet(() -> {
					product.setId(id);
					return ResponseEntity.ok(assembler.toModel(productRepo.save(product)));
				});
	}

	@PatchMapping("/admin/rest/updateProductField/{id}")
	public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody JsonPatch patch) {
		ResponseEntity<EntityModel<Product>> retVal;
		try {
			Product product = productRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
			Product updatedProduct = applyPatchToProduct(patch, product);
			retVal = ResponseEntity.ok(assembler.toModel(productRepo.save(updatedProduct)));
		} catch (JsonPatchException | JsonProcessingException e) {
			retVal = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} catch (ResourceNotFoundException e) {
			retVal = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return retVal;
	}

	@DeleteMapping("/admin/rest/deleteProduct/{id}")
	public ResponseEntity<?> deleteProductById(@PathVariable Long id) {
		productRepo.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/admin/rest/deleteProducts")
	public ResponseEntity<?> deleteProducts() { 
		productRepo.deleteAll();
		return ResponseEntity.noContent().build();
	}
	
	private Product applyPatchToProduct(JsonPatch patch, Product targetProduct) throws JsonPatchException, JsonProcessingException{
		JsonNode patched = patch.apply(objectMapper.convertValue(targetProduct, JsonNode.class));
		return objectMapper.treeToValue(patched, Product.class);
	}
}