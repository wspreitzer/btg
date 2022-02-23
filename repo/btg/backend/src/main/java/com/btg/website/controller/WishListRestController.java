package com.btg.website.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.btg.website.exception.ResourceNotFoundException;
import com.btg.website.model.WishList;
import com.btg.website.repository.WishListRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.util.WishListModelAssembler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

@RestController
public class WishListRestController extends BtgRestController<WishList> {
	@Autowired private WishListRepository wishListRepo;
	private final WishListModelAssembler assembler;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	WishListRestController(WishListModelAssembler assembler) {
		builder = new BtgSpecificationBuilder<WishList>();
		this.assembler = assembler;
	}
	
	@PostMapping("/rest/wishList")
	public ResponseEntity<EntityModel<WishList>> saveWishList(@RequestBody WishList wishList, HttpServletResponse response, HttpServletRequest request) {
		WishList newWishList = wishListRepo.save(wishList);
		return ResponseEntity
				.created(linkTo(methodOn(WishListRestController.class).getWishListById(newWishList.getId())).toUri())
				.header("Location", String.format("%s/btg/rest/wishList/%s", request.getContextPath(), newWishList.getId(), null))
				.body(assembler.toModel(newWishList));
	}
	
	@GetMapping("/rest/wishList/{id}")
	public EntityModel<WishList> getWishListById(@PathVariable Long id) {
		WishList wishList = wishListRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("WishList", id));
		return EntityModel.of(wishList,
				linkTo(methodOn(WishListRestController.class).getWishListById(id)).withSelfRel());
	}
	
	@PatchMapping(path = "/rest/updateWishList/{id}", consumes = "application/json-patch+json")
	public ResponseEntity<EntityModel<WishList>> updateWishList(@PathVariable Long id, @RequestBody JsonPatch patch) {
		ResponseEntity<EntityModel<WishList>> retVal;
		try {
			WishList wishList = wishListRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Wish List", id));
			WishList updatedWishList = applyPatchToWishList(patch, wishList);
			retVal = ResponseEntity.ok(assembler.toModel(wishListRepo.save(updatedWishList)));
		} catch(JsonPatchException | JsonProcessingException e) {
			retVal = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} catch(ResourceNotFoundException e) {
			retVal = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return retVal;
	}
	
	@DeleteMapping("/rest/wishList/{id}")
	public ResponseEntity<?> deleteWishList(@PathVariable Long id) {
		wishListRepo.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	private WishList applyPatchToWishList(JsonPatch patch, WishList targetWishList)  throws JsonPatchException, JsonProcessingException{
		JsonNode patched = patch.apply(objectMapper
				.convertValue(targetWishList, JsonNode.class));
		return objectMapper.treeToValue(patched, WishList.class);
	}
}
