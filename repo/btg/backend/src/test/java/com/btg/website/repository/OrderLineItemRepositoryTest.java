package com.btg.website.repository;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import com.btg.website.model.OrderLineItem;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

@SuppressWarnings("unchecked")
public class OrderLineItemRepositoryTest {

	@MockBean
	OrderLineItemRepository orderLineItemRepo;
	
	private OrderLineItem orderLineItem, orderLineItem2, orderLineItem3, orderLineItem4;
	private List<OrderLineItem> repository;
	private List<OrderLineItem> results;
	
	@BeforeEach
	public void setup() {
		orderLineItemRepo = mock(OrderLineItemRepository.class);
		orderLineItem = new OrderLineItem(null, null, 1, 19.95 );
		orderLineItem2 = new OrderLineItem(null, null, 2, 139.90);
		orderLineItem3 = new OrderLineItem(null, null, 3, 389.85);
		orderLineItem4 = new OrderLineItem(null, null, 4, 51.75);
		repository = setupRepository(orderLineItem, orderLineItem2, orderLineItem3, orderLineItem4);
	}
	
	@Test
	public void returnsNoResultsWhenIdIsNotFound() throws Exception {
		when(orderLineItemRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<OrderLineItem> emptyOrder = orderLineItemRepo.findById(-1L);
		assertThat(false, is(emptyOrder.isPresent()));
	}
	
	@Test
	public void returnsAllOrderLineItemsWhenNoSearchCriteriaIsProvided() throws Exception {
		when(orderLineItemRepo.findAll()).thenReturn(repository);
		List<OrderLineItem> foundOrderLineItems = orderLineItemRepo.findAll();
		assertThat(foundOrderLineItems.size(), is(4));
		assertThat(foundOrderLineItems, containsInAnyOrder(orderLineItem, orderLineItem2, orderLineItem3, orderLineItem4));
	}
	
	@Test
	public void returnsOrderlineItemWhenIdIsFound() throws Exception {
		when(orderLineItemRepo.findById(anyLong())).thenReturn(Optional.of(orderLineItem));
		Optional<OrderLineItem> foundOrderLineItem = orderLineItemRepo.findById(1L);
		assertThat(foundOrderLineItem.get().getQty(), is(1));
		assertThat(foundOrderLineItem.get().getLineTotal(), is(19.95));
	}
	
	@Test
	public void savesOrderLineItemToRepositorySuccessfully() throws Exception {
		OrderLineItem orderLineItemToSave = new OrderLineItem(null, null, 20, 1390.00);
		when(orderLineItemRepo.save(any(OrderLineItem.class))).thenReturn(orderLineItemToSave);
		OrderLineItem newOrderLineItem = orderLineItemRepo.save(orderLineItemToSave);
		assertThat(newOrderLineItem.getQty(), is(20));
		assertThat(newOrderLineItem.getLineTotal(), is(1390.00));
	}
	
	@Test
	public void savesMutipleOrderLineItemToRepositorySuccessfully() throws Exception {
		List<OrderLineItem> listOfOrderLineItemsToSave = new ArrayList<OrderLineItem>();
		OrderLineItem orderLineItemToSave = new OrderLineItem(null, null, 20, 1390.00);
		OrderLineItem orderLineItemToSave2 = new OrderLineItem(null ,null, 10, 69.95);
		
		listOfOrderLineItemsToSave.add(orderLineItemToSave);
		listOfOrderLineItemsToSave.add(orderLineItemToSave2);
		when(orderLineItemRepo.saveAll(anyCollection())).thenReturn(listOfOrderLineItemsToSave);
		List<OrderLineItem> savedOrderLineItems = orderLineItemRepo.saveAll(listOfOrderLineItemsToSave);
		assertThat(savedOrderLineItems.size(), is(2));
		assertThat(savedOrderLineItems, containsInAnyOrder(orderLineItemToSave2, orderLineItemToSave));
	}
	
	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(orderLineItemRepo.count()).thenReturn((long) repository.size());
		long count = orderLineItemRepo.count();
		assertThat(count, is(4L));
	}

	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(orderLineItemRepo).deleteAll();
		orderLineItemRepo.deleteAll();
		verify(orderLineItemRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteOrderLineItemFromRepositoryById() throws Exception {
		when(orderLineItemRepo.findById(anyLong())).thenReturn(Optional.of(orderLineItem4));
		OrderLineItem foundOrderLineItem = orderLineItemRepo.findById(4L).get();
		doAnswer(invocation -> {
			repository.remove(3);
			return null;
		}).when(orderLineItemRepo).deleteById(anyLong());
		orderLineItemRepo.deleteById(4L);
		verify(orderLineItemRepo).deleteById(4L);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(foundOrderLineItem)));
	}
	
	@Test
	public void deleteProvidedCollectionOfOrderLineItemsFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(orderLineItemRepo).deleteAll(anyCollection());
		orderLineItemRepo.deleteAll(repository);
		verify(orderLineItemRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteOrderLineItemsFromRepositoryByGivenIds() throws Exception {
		when(orderLineItemRepo.findAllById(anyCollection())).thenReturn(setupRepository(orderLineItem, orderLineItem2));
		doAnswer(invocation -> {
			Iterable<OrderLineItem> orderLineItemesToDelete = orderLineItemRepo.findAllById(Arrays.asList(1L, 2L));
			orderLineItemesToDelete.forEach(anAddress -> repository.remove(anAddress));
			return null;
		}).when(orderLineItemRepo).deleteAllById(anyCollection());
		orderLineItemRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(orderLineItemRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(orderLineItem)));
		assertThat(repository, not(hasItem(orderLineItem2)));
	}
	
	@Test
	public void deleteProvidedOrderLineItemSuccessfully() throws Exception {
		when(orderLineItemRepo.findById(anyLong())).thenReturn(Optional.of(orderLineItem));
		OrderLineItem foundOrderLineItem = orderLineItemRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(orderLineItemRepo).delete(any(OrderLineItem.class));
		orderLineItemRepo.delete(foundOrderLineItem);
		verify(orderLineItemRepo).delete(foundOrderLineItem);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(orderLineItem)));
	}
	
	@Test
	public void returnsOrderLineItemWhenQtyEquals() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem2));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("qty", SearchOperation.EQUALITY, 2)));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(orderLineItem2));
	}
	
	@Test
	public void returnsOrderLineItemWhenQtyBeginsWith() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("qty", SearchOperation.STARTS_WITH, 1)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(orderLineItem));
	}
	
	@Test
	public void returnsOrderLineItemWhenQtyEndsWith() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem3));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("qty", SearchOperation.ENDS_WITH, 3)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(orderLineItem3));
	}
	
	@Test
	public void returnsOrderLineItemWhenQtyContains() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem4));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("qty", SearchOperation.CONTAINS, 4)));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(orderLineItem4));
	}
	
	@Test
	public void returnsOrderLineItemWhenQtyDoesntEqual() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem ,orderLineItem2, orderLineItem3, orderLineItem4));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("qty", SearchOperation.NEGATION, 100)));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(orderLineItem, orderLineItem2, orderLineItem3, orderLineItem4));
	}
	
	@Test
	public void returnsOrderLineItemWhenQtyIsGreaterThan() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem, orderLineItem2, orderLineItem3, orderLineItem4));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("qty", SearchOperation.GREATER_THAN, 0)));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(orderLineItem ,orderLineItem2, orderLineItem3, orderLineItem4));
	}
	
	@Test
	public void returnsOrderLineItemWhenQtyIsLessThan() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem, orderLineItem2, orderLineItem3, orderLineItem4));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("qty", SearchOperation.NEGATION, 5)));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(orderLineItem, orderLineItem2, orderLineItem3, orderLineItem4));
	}
	
	@Test
	public void returnsOrderLineItemWhenLineTotalEquals() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("lineTotal", SearchOperation.EQUALITY, 19.95)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(orderLineItem));
	}
	
	@Test
	public void returnsOrderLineItemWhenLineTotalBeginsWith() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem3));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("lineTotal", SearchOperation.STARTS_WITH, 38)));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(orderLineItem3));
	}
	
	@Test
	public void returnsOrderLineItemWhenLineTotalEndsWith() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem ,orderLineItem3, orderLineItem4));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("lineTotal", SearchOperation.ENDS_WITH, 5)));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(orderLineItem, orderLineItem3, orderLineItem4));
	}
	
	@Test
	public void returnsOrderLineItemWhenLineTotalContains() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem, orderLineItem3));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("lineTotal", SearchOperation.CONTAINS, .95)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(orderLineItem, orderLineItem3));
	}
	
	@Test
	public void returnsOrderLineItemWhenLineTotalDoesntEqual() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem, orderLineItem2, orderLineItem3, orderLineItem4));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("lineTotal", SearchOperation.NEGATION, 1000.00)));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(orderLineItem, orderLineItem2, orderLineItem3, orderLineItem4));
	}
	
	@Test
	public void returnsOrderLineItemWhenLineTotalIsGreaterThan() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem2, orderLineItem3));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("lineTotal", SearchOperation.GREATER_THAN, 100.00)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(orderLineItem2, orderLineItem3));
	}
	
	@Test
	public void returnsOrderLineItemWhenLineIsLessThan() throws Exception {
		when(orderLineItemRepo.findAll(any(Specification.class))).thenReturn(setupRepository(orderLineItem, orderLineItem4));
		results = orderLineItemRepo.findAll(new BtgSpecification<OrderLineItem>(new SearchCriteria("lineTotal", SearchOperation.LESS_THAN, 100.00)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(orderLineItem, orderLineItem4));
	}
	
	private List<OrderLineItem> setupRepository(OrderLineItem...orderLineItems) {
		List<OrderLineItem> orderLineItemsList = new ArrayList<OrderLineItem>();
		Arrays.asList(orderLineItems).forEach(orderLineItem -> {
			orderLineItemsList.add((OrderLineItem)orderLineItem);
		});
		
		return orderLineItemsList;
	}
}