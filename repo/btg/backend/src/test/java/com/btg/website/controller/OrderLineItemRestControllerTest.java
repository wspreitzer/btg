package com.btg.website.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import com.btg.website.model.OrderLineItem;
import com.btg.website.repository.OrderLineItemRepository;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonConfig.class})
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK, classes = { WebsiteApplication.class} )
@SuppressWarnings("unchecked")
public class OrderLineItemRestControllerTest {

	@MockBean OrderLineItemRepository orderItemRepo;
	
	@Autowired private WebApplicationContext webApplicationContext;
	
	private OrderLineItem orderItem, orderItem2, orderItem3, orderItem4;
	private TestUtils<OrderLineItem> orderItemUtil;
	private List<OrderLineItem> orderItemList;
	private MockMvc mockedRequest;
	private Order order;
	
	@BeforeEach
	public void setup() {
		order = new Order("btg-0111224566", null, null, 250.00, 25.00, 19.95,294.95, null);
		orderItemUtil = new TestUtils<OrderLineItem>();
		orderItem = new OrderLineItem(order, null, 1, 19.95);
		orderItem2 = new OrderLineItem(order, null, 2, 139.90);
		orderItem3 = new OrderLineItem(order, null, 3, 389.85);
		orderItem4 = new OrderLineItem(order, null, 4, 51.75);
		orderItemList =  orderItemUtil.setupRepository(orderItem, orderItem2, orderItem3, orderItem4);
		this.mockedRequest = webAppContextSetup(webApplicationContext).build();
	}                                           

	@Test
	public void returnsLineItemById() throws Exception {
		when(orderItemRepo.findById(anyLong())).thenReturn(Optional.of(orderItem));
		MvcResult mvcResult = mockedRequest.perform(get("/btg/rest/orderLineItem/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.qty").value(Integer.valueOf("1")))
				.andExpect(jsonPath("$.lineTotal").value(Double.valueOf("19.95"))).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsAllOrderLineItems() throws Exception {
		when(orderItemRepo.findAll(any(Specification.class))).thenReturn(orderItemUtil.setupRepository(orderItem, orderItem2, orderItem3, orderItem4));
		MvcResult mvcResult = mockedRequest.perform(get("/btg/rest/orderLineItems/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
}
