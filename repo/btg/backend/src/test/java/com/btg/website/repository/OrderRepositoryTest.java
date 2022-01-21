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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import com.btg.website.model.Order;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

public class OrderRepositoryTest {
	
	@MockBean
	OrderRepository orderRepo;
	
	private Order order, order2, order3, order4, order5;
	
	private List<Order> repository;
	private List<Order> results;

	@BeforeEach
	public void setup() {
		orderRepo = mock(OrderRepository.class);
		order = new Order("btg-0111224566", new Date(System.currentTimeMillis()),
					null, null, 250.00, 25.00, 19.95, 294.95, "new");
		order2 = new Order("2btg-0111224567", new Date(System.currentTimeMillis()),
				    null, null, 300.000, 30.000, 29.95, 359.95, "picking");
		order3 = new Order("btg-0111224568", new Date(System.currentTimeMillis()),
				    null, null, 500.00, 50.00, 19.95, 569.95, "picked");
		order4 = new Order("2btg-0111224569", new Date(System.currentTimeMillis()),
				    null, null, 100.000, 10.000, 0.000, 110.00, "loading");
		order5 = new Order("btg-0111224570", new Date(System.currentTimeMillis()),
				null, null, 100.00, 10.00, 0.000, 110.00, "shipped");
		repository = setupRepository(order, order2, order3, order4, order5);
	}
	
	@Test
	public void returnsNoResultsWhenIdIsNotFound() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<Order> emptyOrder = orderRepo.findById(-1L);
		assertThat(false, is(emptyOrder.isPresent()));
	}
	
	@Test
	public void returnsAllOrdersWhenNoSearchCriteriaIsProvided() throws Exception {
		when(orderRepo.findAll()).thenReturn(repository);
		List<Order> foundOrders = orderRepo.findAll();
		assertThat(foundOrders.size(), is(5));
		assertThat(foundOrders, containsInAnyOrder(order, order2, order3, order4, order5));
	}
	
	@Test
	public void returnsOrderWhenIdisFound() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order));
		Optional<Order> foundOrder = orderRepo.findById(1L);
		assertThat(true, is(foundOrder.isPresent()));
		assertThat(foundOrder.get().getOrderNumber(), is("btg-0111224566"));
		assertThat(foundOrder.get().getOrderDate(), is(order.getOrderDate()));
		assertThat(foundOrder.get().getOrderSubTotal(), is(250.00));
		assertThat(foundOrder.get().getOrderTax(), is(25.00));
		assertThat(foundOrder.get().getOrderShipping(), is(19.95));
		assertThat(foundOrder.get().getOrderTotal(), is(294.95));
		assertThat(foundOrder.get().getOrderStatus(), is("new"));
	}

	@Test
	public void savesOrderToRepositorySuccessfully() throws Exception {
		Order orderToSave = new Order("btg-01111224577", new Date(System.currentTimeMillis()),
				null, null, 22.00, 2.20, 0.0, 24.20, "delivered");
		when(orderRepo.save(any(Order.class))).thenReturn(orderToSave);
		Order newOrder = orderRepo.save(orderToSave);
		assertThat(newOrder.getOrderNumber(), is("btg-01111224577"));
		assertThat(newOrder.getOrderDate(), is(orderToSave.getOrderDate()));
		assertThat(newOrder.getOrderSubTotal(), is(22.00));
		assertThat(newOrder.getOrderTax(), is(2.20));
		assertThat(newOrder.getOrderShipping(), is(0.00));
		assertThat(newOrder.getOrderTotal(), is(24.20));
		assertThat(newOrder.getOrderStatus(), is("delivered"));
	}
	
	@Test
	public void savesMutipleOrderToRepositorySuccessfully() throws Exception {
		List<Order> listOfOrdersToSave = new ArrayList<Order>();
		Order orderToSave = new Order("btg-01111224577", new Date(System.currentTimeMillis()),
				null, null, 22.00, 2.20, 0.0, 24.20, "delivered");
		Order orderToSave2 = new Order("btg-01111224578", new Date(System.currentTimeMillis()),
				null, null, 220.00, 22.00, 0.0, 244.00, "new");
		
		listOfOrdersToSave.add(orderToSave);
		listOfOrdersToSave.add(orderToSave2);
		when(orderRepo.saveAll(anyCollection())).thenReturn(listOfOrdersToSave);
		List<Order> savedOrders = orderRepo.saveAll(listOfOrdersToSave);
		assertThat(savedOrders.size(), is(2));
		assertThat(savedOrders, containsInAnyOrder(orderToSave2, orderToSave));
	}
	
	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(orderRepo.count()).thenReturn((long) repository.size());
		long count = orderRepo.count();
		assertThat(count, is(5L));
	}

	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(orderRepo).deleteAll();
		orderRepo.deleteAll();
		verify(orderRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteOrderFromRepositoryById() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order4));
		Order foundOrder = orderRepo.findById(4L).get();
		doAnswer(invocation -> {
			repository.remove(3);
			return null;
		}).when(orderRepo).deleteById(anyLong());
		orderRepo.deleteById(4L);
		verify(orderRepo).deleteById(4L);
		assertThat(repository.size(), is(4));
		assertThat(repository, not(hasItem(foundOrder)));
	}
	
	@Test
	public void deleteProvidedCollectionOfOrdersFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(orderRepo).deleteAll(anyCollection());
		orderRepo.deleteAll(repository);
		verify(orderRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteOrdersFromRepositoryByGivenIds() throws Exception {
		when(orderRepo.findAllById(anyCollection())).thenReturn(setupRepository(order, order2));
		doAnswer(invocation -> {
			Iterable<Order> orderesToDelete = orderRepo.findAllById(Arrays.asList(1L, 2L));
			orderesToDelete.forEach(anAddress -> repository.remove(anAddress));
			return null;
		}).when(orderRepo).deleteAllById(anyCollection());
		orderRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(orderRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(order)));
		assertThat(repository, not(hasItem(order2)));
	}
	
	@Test
	public void deleteProvidedOrderSuccessfully() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order));
		Order foundOrder = orderRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(orderRepo).delete(any(Order.class));
		orderRepo.delete(foundOrder);
		verify(orderRepo).delete(foundOrder);
		assertThat(repository.size(), is(4));
		assertThat(repository, not(hasItem(order)));
	}
	
	@Test
	public void returnsOrderWhenOrderNumberEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderNumber", SearchOperation.EQUALITY, "btg-0111224566")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(order));
	}
	
	@Test
	public void returnsOrderWhenOrderNumberBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order2));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderNumber", SearchOperation.STARTS_WITH, "2btg")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(order2));
	}
	
	@Test
	public void returnsOrderWhenOrderNumberEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order3));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderNumber", SearchOperation.ENDS_WITH, "68")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(order3));
	}
	
	@Test
	public void returnsOrderWhenOrderNumberContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order4, order2));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderNumber", SearchOperation.CONTAINS, "2btg-")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order4, order2));
	}
	
	@Test
	public void returnsOrderWhenOrderNumberDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order2, order3, order4));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderNumber", SearchOperation.NEGATION, "btg-0111224566")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(order2, order3, order4));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void returnsOrderWhenOrderSubtotalEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderSubtotal", SearchOperation.EQUALITY, 250.00)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(order));
	}
	
	@Test
	public void returnsOrderWhenOrderSubtotalBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order4, order5));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderSubtotal", SearchOperation.STARTS_WITH, 10)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order4, order5));
	}
	
	@Test
	public void returnsOrderWhenOrderSubtotalEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class)))
			.thenReturn(setupRepository(order2, order4));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria
				("orderSubtotal", SearchOperation.ENDS_WITH, .000)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order2, order4));
	}
	
	@Test
	public void returnsOrderWhenOrderSubtotalContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order, order2, order3, order4, order5));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderSubtotal", SearchOperation.CONTAINS, 0.0)));
		assertThat(results.size(), is(5));
		assertThat(results, containsInAnyOrder(order, order2, order3, order4, order5));
	}
	
	@Test
	public void returnsOrderWhenOrderSubtotalDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order2, order3, order4, order5));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderSubtotal", SearchOperation.NEGATION, 250.00)));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(order2, order3, order4, order5));
	}
	
	@Test
	public void returnsOrderWhenOrderSubtotalIsGreaterThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(
				order, order2, order3));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderSubTotal", SearchOperation.GREATER_THAN, 101.00)));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(order, order2, order3));
	}
	
	@Test
	public void returnsOrderWhenOrderSubtotalIsLessThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(
				order4, order5));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderSubTotal", SearchOperation.GREATER_THAN, 200.00)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order4, order5));		
	}
	
	@Test
	public void returnsOrderWhenOrderTaxEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTax", SearchOperation.EQUALITY, 25.00)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(order));
	}
	
	@Test
	public void returnsOrderWhenOrderTaxBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order4, order5));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTax", SearchOperation.STARTS_WITH, 10)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order4, order5));
	}
	
	@Test
	public void returnsOrderWhenOrderTaxEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order2, order4));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTax", SearchOperation.ENDS_WITH, .000)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order2, order4));
	}
	
	@Test
	public void returnsOrderWhenOrderTaxContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order, order2, order3, order4, order5));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTax", SearchOperation.CONTAINS, 0.0)));
		assertThat(results.size(), is(5));
		assertThat(results, containsInAnyOrder(order, order2, order3, order4, order5));
	}
	
	@Test
	public void returnsOrderWhenOrderTaxDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order2, order3, order4));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTax", SearchOperation.NEGATION, "222-805-2222")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(order2, order3, order4));
	}
	
	@Test
	public void returnsOrderWhenOrderTaxIsGreaterThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(
				order, order2, order3));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderTax", SearchOperation.GREATER_THAN, 10.00)));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(order, order2, order3));
	}
	
	@Test
	public void returnsOrderWhenOrderTaxIsLessThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order4, order5));
		results = orderRepo.findAll(new BtgSpecification<>(
				new SearchCriteria("orderTax", SearchOperation.LESS_THAN, 20.00)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order4, order5));
	}
	
	public void returnsOrderWhenOrderShippingEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderShipping", SearchOperation.EQUALITY, 19.95)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(order));
	}
	
	@Test
	public void returnsOrderWhenOrderShippingBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order4, order5));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderShipping", SearchOperation.STARTS_WITH, 0.0)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order4, order5));
	}
	
	@Test
	public void returnsOrderWhenOrderShippingEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order5, order4));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderShipping", SearchOperation.ENDS_WITH, .000)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order5, order4));
	}
	
	@Test
	public void returnsOrderWhenOrderShippingContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order, order2, order3));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderShipping", SearchOperation.CONTAINS, 9.9)));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(order, order2, order3));
	}
	
	@Test
	public void returnsOrderWhenOrderShippingDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order, order2, order3));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderShipping", SearchOperation.NEGATION, 0.000)));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(order, order2, order3));
	}
	
	@Test
	public void returnsOrderWhenOrderShippingIsGreaterThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order2));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderShipping", SearchOperation.GREATER_THAN, 20.00)));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(order2));
	}
	
	@Test
	public void returnsOrderWhenOrderShippingIsLessThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order, order3, order4, order5));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderShipping", SearchOperation.LESS_THAN, 20.00)));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(order, order3, order4, order5));
	}
	
	@Test
	public void returnsOrderWhenOrderTotalEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTotal", SearchOperation.EQUALITY, 294.95)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(order));
	}
	
	@Test
	public void returnsOrderWhenOrderTotalBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order4, order5));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTotal", SearchOperation.STARTS_WITH, 11)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order4, order5));
	}
	
	@Test
	public void returnsOrderWhenOrderTotalEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order, order2, order3));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTotal", SearchOperation.ENDS_WITH, .95)));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(order, order2, order3));
	}
	
	@Test
	public void returnsOrderWhenOrderTotalContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order2, order3));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTotal", SearchOperation.CONTAINS, 9.95)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order2, order3));
	}
	
	@Test
	public void returnsOrderWhenOrderTotalDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order, order2, order3, order4, order5));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTotal", SearchOperation.CONTAINS, 0.00)));
		assertThat(results.size(), is(5));
		assertThat(results, containsInAnyOrder(order, order2, order3, order4, order5));
		
	}
	
	@Test
	public void returnsOrderWhenOrderTotalIsGreaterThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order, order2, order3));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTotal", SearchOperation.CONTAINS, 200.00)));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(order, order2, order3));
		
	}
	
	@Test
	public void returnsOrderWhenOrderTotalIsLessThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order4, order5));
		results = orderRepo.findAll(new BtgSpecification<Order>(new SearchCriteria("orderTotal", SearchOperation.CONTAINS, 200.00)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order4, order5));
		
	}
	
	@Test
	public void returnsOrderWhenOrderStatusEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderStatus", SearchOperation.EQUALITY, "new")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(order));
	}
	
	@Test
	public void returnsOrderWhenOrderStatusBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order2, order3));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderStatus", SearchOperation.STARTS_WITH, "pic")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order2, order3));
	}
	
	@Test
	public void returnsOrderWhenOrderStatusEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order2, order4));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderStatus", SearchOperation.ENDS_WITH, "ing")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(order2, order4));
	}
	
	@Test
	public void returnsOrderWhenOrderStatusContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order, order5, order3));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderStatus", SearchOperation.CONTAINS, "e")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(order, order5, order3));
	}
	
	@Test
	public void returnsOrderWhenOrderStatusDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(setupRepository(order, order2, order3, order4));
		results = orderRepo.findAll(new BtgSpecification<Order>(
				new SearchCriteria("orderStatus", SearchOperation.NEGATION, "shipped")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(order, order2, order3, order4));
	}

	private List<Order> setupRepository(Order...orders) {
		List<Order> ordersList = new ArrayList<Order>();
		Arrays.asList(orders).forEach(order -> {
			ordersList.add((Order) order);
		});
		return ordersList;
	}
}