package com.btg.website.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.btg.website.WebsiteApplication;
import com.btg.website.config.JacksonConfig;
import com.btg.website.model.Order;
import com.btg.website.model.Status;
import com.btg.website.repository.OrderRepository;
import com.btg.website.util.TestUtils;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonConfig.class})
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK, classes= { WebsiteApplication.class })
@SuppressWarnings("unchecked")
public class OrderRestControllerTest {

	@MockBean private OrderRepository orderRepo;
	
	@InjectMocks OrderRestController controller;
	
	@Autowired private WebApplicationContext webApplicationContext;

	private Order order, order2, order3, order4, order5, order6, order7, order8;
	private List<Order> orderList;
	private TestUtils<Order> orderUtils;
	private MockMvc mockedRequest;
	private Date orderDate;
	
	@BeforeEach
	public void setup() {
		orderDate = new Date(System.currentTimeMillis());
		orderUtils = new TestUtils<Order>();
		
		order = new Order("btg-0111224566", null, null, 250.00, 25.00, 19.95,
				294.95, null);
		order2 = new Order("2btg-0111224567", null, null, 300.000, 30.000, 29.95,
				359.95, null);
		order3 = new Order("btg-0111224568", null, null, 500.00, 50.00, 19.95,
				569.95, null);
		order4 = new Order("2btg-0111224569", null, null, 100.000, 10.000, 0.000,
				110.00, null);
		order5 = new Order("btg-0111224570", null, null, 100.00, 10.00, 0.000,
				110.00, null);
		order6 = new Order("btg-0112224571", null, null, 100.00, 10.00, 0.000,
				110.00, null);
		order7 = new Order("btg-0112224572", null, null, 100.00, 10.00, 0.000,
				110.00, null);
		order8 = new Order("btg-0112224573", null, null, 100.00, 10.00, 0.000,
				110.00, null);
		order.setOrderDate(orderDate);
		order2.setOrderDate(orderDate);
		order3.setOrderDate(orderDate);
		order4.setOrderDate(orderDate);
		order5.setOrderDate(orderDate);
		order6.setOrderDate(orderDate);
		order7.setOrderDate(orderDate);
		order8.setOrderDate(orderDate);
		order.setOrderStatus(Status.NEW);
		order2.setOrderStatus(Status.PICKING);
		order3.setOrderStatus(Status.PICKED);
		order4.setOrderStatus(Status.LOADING);
		order5.setOrderStatus(Status.LOADED);
		order6.setOrderStatus(Status.SHIPPED);
		order7.setOrderStatus(Status.COMPLETED);
		order8.setOrderStatus(Status.CANCELED);
		orderList = orderUtils.setupRepository(order, order2, order3, order4, order5, order6, order7, order8);
		this.mockedRequest = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void returnsCustomerOrders() throws Exception {
		when(orderRepo.findAll()).thenReturn(orderList);
		MvcResult mvcResult = mockedRequest.perform(get("/btg/rest/orders/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrderById() throws Exception {
		when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
		
		MvcResult mvcResult = mockedRequest.perform(get("/btg/rest/orders/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderNumber").value("btg-0111224566"))
				.andExpect(jsonPath("$.orderDate").value(orderDate.toString()))
				.andExpect(jsonPath("$.orderSubTotal").value(Double.valueOf("250.00")))
				.andExpect(jsonPath("$.orderTax").value(Double.valueOf("25.00")))
				.andExpect(jsonPath("$.orderShipping").value(Double.valueOf("19.95")))
				.andExpect(jsonPath("$.orderTotal").value(Double.valueOf("294.95")))
				.andExpect(jsonPath("$.orderStatus").value(Status.NEW.toString())).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void createOrderWhenValidRequest() throws Exception {
		Order orderToSave = new Order("btg-0127224588", null, null, 300.00, 30.00, 0.00, 330.00, null);
		orderToSave.setOrderDate(orderDate);
		orderToSave.setOrderStatus(Status.NEW);
		when(orderRepo.save(any(Order.class))).thenReturn(orderToSave);
		MvcResult mvcResult = mockedRequest.perform(post("/btg/rest/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"orderNumber\" : \"btg-0127224588\", \"orderDate\":\"" + orderDate + "\",\"Customer\" : {}, \"OrderLineItem\" : {},  \"orderSubtotal\" : \"300.00\", \"orderTax\" : \"30.00\", \"orderShippint\" : \"0.00\", \"orderTotal\" : \"330.00\", \"CreditCard\" : {}, \"orderStatus\" : \"" + Status.NEW + "\"}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "http://localhost/btg/rest/orders/0"))
				.andExpect(jsonPath("$.id").value("0"))
				.andExpect(jsonPath("$.orderNumber").value("btg-0127224588"))
				.andExpect(jsonPath("$.orderDate").value(orderDate.toString()))
				.andExpect(jsonPath("$.orderSubTotal").value(Double.valueOf("300.00")))
				.andExpect(jsonPath("$.orderTax").value(Double.valueOf("30.00")))
				.andExpect(jsonPath("$.orderShipping").value(Double.valueOf("0.00")))
				.andExpect(jsonPath("$.orderTotal").value(Double.valueOf("330.00")))
				.andExpect(jsonPath("$.orderStatus").value("NEW")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void deleteOrderByIdWhenRequestIsValid() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order5));
		Order foundOrder = orderRepo.findById(5L).get();
		doAnswer(invocation -> {
			orderList.remove(4);
			return null;
		}).when(orderRepo).deleteById(anyLong());
		mockedRequest.perform(delete("/btg/rest/orders/5")).andExpect(status().isNoContent());
		verify(orderRepo).deleteById(5L);
		assertThat(orderList.size(), is(7));
		assertThat(orderList, not(hasItem(foundOrder)));
	}
	
	@Test
	public void returnsOrderWhenOrderNumberEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderNumber:btg-0111224566")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsOrderWhenOrderNumberBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order2, order4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderNumber:2btg*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsOrderWhenOrderNumberEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderNumber:*68")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrderWhenOrderNumberContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order2, order4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderNumber:*2btg*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrderWhenOrderNumberDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order2, order3, order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderNumber!btg-0111224566")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsOrdersWhenOrderSubtotalEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order));
		MvcResult mvcResult = mockedRequest
			.perform(get("/btg/rest/ordersBySpecification/")
					.param("search", "orderSubTotal:250.00")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderSubtotalBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderSubTotal:10*")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderSubtotalEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order2, order4));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderSubTotal:*.000")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderSubtotalContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order2, order3, order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderSubTotal:*0.0*")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderSubtotalDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order2, order3, order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderSubTotal!250.00")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderSubtotalIsGreaterThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order2, order3));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderSubTotal>101.00")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderSubtotalIsLessThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderSubTotal<200.00")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTaxEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderTax:25.00")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTaxBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderTax:10*")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTaxEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order2, order4));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderTax:*.000")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTaxContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order2, order3, order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderTax:*0.0*")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTaxDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order2, order3, order4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderTax!10.00")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTaxIsGreaterThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order2, order3));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderTax>10.00")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsOrdersWhenOrderTaxIsLessThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderTax<20.00")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderShippingEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderShipping:19.95")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsOrdersWhenOrderShippingBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/ordersBySpecification/")
				.param("search", "orderShipping:0.0*")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderShippingEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order4, order5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderShipping:*.000")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderShippingContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order2, order3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderSubTotal:*9.9*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderShippingDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order2, order3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderSubTotal!0.00")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderShippingIsGreaterThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderSubTotal>20.00")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderShippingIsLessThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order3, order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderShipping<20.00")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTotalEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderTotal:294.95")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTotalBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification")
						.param("search", "orderTotal:11*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTotalEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order2, order3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification")
						.param("search", "orderTotal:*.95")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTotalContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order2, order3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderTotal:*9.95*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTotalDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order2, order3, order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderTotal!0.0")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTotalIsGreaterThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order2, order3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderTotal>200.0")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderTotalIsLessThan() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order4, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderTotal<200.0")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderStatusEquals() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderStatus:NEW")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderStatusBeginsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order2, order3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderStatus:PIC*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderStatusEndsWith() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order2, order4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderStatus:*ING")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsOrdersWhenOrderStatusContains() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order3, order5, order6, order7, order8));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderStatus:*E*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsOrdersWhenOrderStatusDoesntEqual() throws Exception {
		when(orderRepo.findAll(any(Specification.class))).thenReturn(orderUtils.setupRepository(order, order3, order5, order6, order7, order4, order2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/ordersBySpecification/")
						.param("search", "orderStatus!SHIPPED")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void cancelsOrderWhenStatusIsNew() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order));
		Order updatedOrder = new Order();
		updatedOrder.setOrderStatus(Status.CANCELED);
		when(orderRepo.save(any(Order.class))).thenReturn(updatedOrder);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/orders/1")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void cancelsOrderWhenStatusIsPicking() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order2));
		Order updatedOrder = new Order();
		updatedOrder.setOrderStatus(Status.CANCELED);
		when(orderRepo.save(any(Order.class))).thenReturn(updatedOrder);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/orders/2")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void cancelsOrderWhenStatusIsPicked() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order3));
		Order updatedOrder = new Order();
		updatedOrder.setOrderStatus(Status.CANCELED);
		when(orderRepo.save(any(Order.class))).thenReturn(updatedOrder);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/orders/3")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void handlesMethodNotAllowedOrderWhenStatusIsNotValid() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order4));
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/orders/4")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isMethodNotAllowed()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesOrderStatusWhenOrderStatusIsNewAndRequestIsVaild() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order)); 
		Order updatedOrder = new Order(order.getOrderNumber(), null, null, order.getOrderSubTotal(), order.getOrderTax(), order.getOrderShipping(), order.getOrderTotal(), null);
		updatedOrder.setOrderId(order.getId());
		updatedOrder.setOrderStatus(Status.PICKING);
		when(orderRepo.save(any(Order.class))).thenReturn(updatedOrder);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/1/PICKING")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesOrderStatusWhenOrderStatusIsPickingAndRequestIsVaild() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order2)); 
		Order updatedOrder = new Order(order2.getOrderNumber(), null, null, order2.getOrderSubTotal(), order2.getOrderTax(), order2.getOrderShipping(), order2.getOrderTotal(), null);
		updatedOrder.setOrderId(order2.getId());
		updatedOrder.setOrderStatus(Status.PICKED);
		when(orderRepo.save(any(Order.class))).thenReturn(updatedOrder);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/2/PICKED")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesOrderStatusWhenOrderStatusIsPickedAndRequestIsVaild() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order3)); 
		Order updatedOrder = new Order(order3.getOrderNumber(), null, null, order3.getOrderSubTotal(), order3.getOrderTax(), order3.getOrderShipping(), order3.getOrderTotal(), null);
		updatedOrder.setOrderId(order3.getId());
		updatedOrder.setOrderStatus(Status.LOADING);
		when(orderRepo.save(any(Order.class))).thenReturn(updatedOrder);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/3/LOADING")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesOrderStatusWhenOrderStatusIsLoadingAndRequestIsVaild() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order4)); 
		Order updatedOrder = new Order(order4.getOrderNumber(), null, null, order4.getOrderSubTotal(), order4.getOrderTax(), order4.getOrderShipping(), order4.getOrderTotal(), null);
		updatedOrder.setOrderId(order.getId());
		updatedOrder.setOrderStatus(Status.LOADED);
		when(orderRepo.save(any(Order.class))).thenReturn(updatedOrder);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/4/LOADED")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesOrderStatusWhenOrderStatusIsLoadedAndRequestIsVaild() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order5)); 
		Order updatedOrder = new Order(order5.getOrderNumber(), null, null, order5.getOrderSubTotal(), order5.getOrderTax(), order5.getOrderShipping(), order5.getOrderTotal(), null);
		updatedOrder.setOrderId(order.getId());
		updatedOrder.setOrderStatus(Status.PICKING);
		when(orderRepo.save(any(Order.class))).thenReturn(updatedOrder);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/5/SHIPPED")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesOrderStatusWhenOrderStatusIsShippedAndRequestIsVaild() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order6)); 
		Order updatedOrder = new Order(order6.getOrderNumber(), null, null, order6.getOrderSubTotal(), order6.getOrderTax(), order6.getOrderShipping(), order6.getOrderTotal(), null);
		updatedOrder.setOrderId(order.getId());
		updatedOrder.setOrderStatus(Status.COMPLETED);
		when(orderRepo.save(any(Order.class))).thenReturn(updatedOrder);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/6/COMPLETED")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void updatesOrderStatusWhenOrderStatusIsCompletedAndRequestIsVaild() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order7)); 
		Order updatedOrder = new Order(order7.getOrderNumber(), null, null, order7.getOrderSubTotal(), order7.getOrderTax(), order7.getOrderShipping(), order7.getOrderTotal(), null);
		updatedOrder.setOrderId(order.getId());
		updatedOrder.setOrderStatus(Status.ARCHIVE);
		when(orderRepo.save(any(Order.class))).thenReturn(updatedOrder);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/7/ARCHIVE")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void updatesOrderStatusWhenOrderStatusIsCanceledAndRequestIsVaild() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order8)); 
		Order updatedOrder = new Order(order8.getOrderNumber(), null, null, order8.getOrderSubTotal(), order8.getOrderTax(), order8.getOrderShipping(), order8.getOrderTotal(), null);
		updatedOrder.setOrderId(order.getId());
		updatedOrder.setOrderStatus(Status.ARCHIVE);
		when(orderRepo.save(any(Order.class))).thenReturn(updatedOrder);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/8/ARCHIVE")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void handlesMethodNotAllowedWhenOrderStatusIsNewAndRequestIsNotValid() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order));
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/1/COMPLETED")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isMethodNotAllowed()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void handlesMethodNotAllowedWhenOrderStatusIsPickingAndRequestIsNotValid() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order2));
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/2/COMPLETED")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isMethodNotAllowed()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void handlesMethodNotAllowedWhenOrderStatusIsPickedAndRequestIsNotValid() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order3));
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/3/COMPLETED")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isMethodNotAllowed()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void handlesMethodNotAllowedWhenOrderStatusIsLoadingAndRequestIsNotValid() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order4));
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/4/COMPLETED")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isMethodNotAllowed()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void handlesMethodNotAllowedWhenOrderStatusIsLoadedAndRequestIsNotValid() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order5));
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/5/COMPLETED")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isMethodNotAllowed()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void handlesMethodNotAllowedWhenOrderStatusIsShippedAndRequestIsNotValid() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order6));
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/6/LOADED")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isMethodNotAllowed()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void handlesMethodNotAllowedWhenOrderStatusIsCompletedAndRequestIsNotValid() throws Exception {
		when(orderRepo.findById(anyLong())).thenReturn(Optional.of(order7));
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/admin/orders/7/LOADED")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isMethodNotAllowed()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
}