package com.btg.website;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.btg.website.model.Cart;
import com.btg.website.repository.CartRepository;

public class CartRepositoryTest {

	@MockBean
	CartRepository cartRepo;

	private Cart cart, cart2, cart3, cart4;
	
	private List<Cart> repository;
	
	@BeforeEach
	public void setup() {
		cartRepo = mock(CartRepository.class);

		cart = new Cart(null, null, new Date(System.currentTimeMillis()));
		cart2 = new Cart(null, null, new Date(System.currentTimeMillis() - 254854));
		cart3 = new Cart(null, null, new Date(System.currentTimeMillis() - 300000));
		cart4 = new Cart(null, null, new Date(System.currentTimeMillis() - 100000));
		
		repository = setupRepository(cart, cart2, cart3, cart4);
	}
	
	@Test
	public void returnsNoResultsWhenIdIsNotFound() throws Exception {
		when(cartRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<Cart> emptyCart = cartRepo.findById(-1L);
		assertThat(false, is(emptyCart.isPresent()));
	}
	
	@Test
	public void returnsAllCartsWhenNoSearchCriteriaIsProvided() throws Exception {
		when(cartRepo.findAll()).thenReturn(repository);
		List<Cart> foundCarts = cartRepo.findAll();
		assertThat(foundCarts.size(), is(4));
		assertThat(foundCarts, containsInAnyOrder(cart, cart2, cart3, cart4));
	}
	
	@Test
	public void returnsCartWhenIdIsFound() throws Exception {
		when(cartRepo.findById(anyLong())).thenReturn(Optional.of(cart));
		Optional<Cart> foundCart = cartRepo.findById(1L);
		assertThat(true, is(foundCart.isPresent()));		
	}
	
	@Test
	public void savesCartToRepositorySuccessfully() throws Exception {
		Cart cartToSave = new Cart();
		when(cartRepo.save(any(Cart.class))).thenReturn(cartToSave);
		Cart newCart = cartRepo.save(cartToSave);
		assertThat(newCart.getCreatedDate(), is(cartToSave.getCreatedDate()));
	}
	
	@Test
	public void savesMutipleCartToRepositorySuccessfully() throws Exception {
		List<Cart> listOfCartsToSave = new ArrayList<Cart>();
		Cart cartToSave = new Cart();
		Cart cartToSave2 = new Cart();
		listOfCartsToSave.add(cartToSave);
		listOfCartsToSave.add(cartToSave2);
		when(cartRepo.saveAll(anyCollection())).thenReturn(listOfCartsToSave);
		List<Cart> savedCarts = cartRepo.saveAll(listOfCartsToSave);
		assertThat(savedCarts.size(), is(2));
		assertThat(savedCarts, containsInAnyOrder(cartToSave2, cartToSave));
	}
	
	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(cartRepo.count()).thenReturn((long) repository.size());
		long count = cartRepo.count();
		assertThat(count, is(4L));
	}

	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(cartRepo).deleteAll();
		cartRepo.deleteAll();
		verify(cartRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteCartFromRepositoryById() throws Exception {
		when(cartRepo.findById(anyLong())).thenReturn(Optional.of(cart4));
		Cart foundCart = cartRepo.findById(4L).get();
		doAnswer(invocation -> {
			repository.remove(3);
			return null;
		}).when(cartRepo).deleteById(anyLong());
		cartRepo.deleteById(4L);
		verify(cartRepo).deleteById(4L);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(foundCart)));
	}
	
	@Test
	public void deleteProvidedCollectionOfCartsFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(cartRepo).deleteAll(anyCollection());
		cartRepo.deleteAll(repository);
		verify(cartRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteCartsFromRepositoryByGivenIds() throws Exception {
		when(cartRepo.findAllById(anyCollection())).thenReturn(setupRepository(cart, cart2));
		doAnswer(invocation -> {
			Iterable<Cart> cartesToDelete = cartRepo.findAllById(Arrays.asList(1L, 2L));
			cartesToDelete.forEach(anAddress -> repository.remove(anAddress));
			return null;
		}).when(cartRepo).deleteAllById(anyCollection());
		cartRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(cartRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(cart)));
		assertThat(repository, not(hasItem(cart2)));
	}
	
	@Test
	public void deleteProvidedCartSuccessfully() throws Exception {
		when(cartRepo.findById(anyLong())).thenReturn(Optional.of(cart));
		Cart foundCart = cartRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(cartRepo).delete(any(Cart.class));
		cartRepo.delete(foundCart);
		verify(cartRepo).delete(foundCart);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(cart)));
	}
	
	private List<Cart> setupRepository(Cart...carts) {
		List<Cart> cartList = new ArrayList<Cart>();
		Arrays.asList(carts).forEach(aCart ->{
			cartList.add((Cart) aCart);
		});
		return cartList;
	}
}
