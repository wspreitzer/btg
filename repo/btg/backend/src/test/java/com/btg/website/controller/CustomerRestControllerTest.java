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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.btg.website.WebsiteApplication;
import com.btg.website.config.JacksonConfig;
import com.btg.website.model.Customer;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonConfig.class})
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK, classes = {WebsiteApplication.class})
@SuppressWarnings("unchecked")
public class CustomerRestControllerTest {

	@MockBean private CustomerRepository customerRepo;
	
	@InjectMocks CustomerRestController controller;
	
	@Autowired private WebApplicationContext webApplicationContext;
	
	private Customer customer, customer2, customer3, customer4;
	
	private List<Customer> customerList;
	
	private MockMvc mockedRequest;
	
	private Date signUpDate;
	
	private TestUtils<Customer> customerUtils;
	@BeforeEach
	public void setup() {
		customer = new Customer("Bob", "Smith", null, null, null, "bob.smith@comcast.com", 
				"222-805-2222", "user1", "p@ssword", null, null);
		customer2 = new Customer("John", "smythe", null, null, null, "john.smythe@comcast.net", 
				"312-781-1916", "user2", "Pword", null, null);
		customer3 = new Customer("Jon", "Doe", null, null, null, "jon.doe@company.com", 
				"312-693-0103", "jdoe", "P@ssw0rd", null, null);
		customer4 = new Customer("Tom", "Garcia", null, null, null, "tgarcia@company2.net", 
				"773-805-3203", "tgarcia", "!P@ssW0rd", null, null);
		customerUtils = new TestUtils<Customer>();
		signUpDate = new Date(System.currentTimeMillis());
		
		customer.setSignupDate(signUpDate);
		customer2.setSignupDate(signUpDate);
		customer3.setSignupDate(signUpDate);
		customer4.setSignupDate(signUpDate);
		
		customerList = customerUtils.setupRepository(customer, customer2, customer3, customer4);
		this.mockedRequest = webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void returns404WhenCustomerIsNotFoundById() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest.perform(get("/rest/customers/0")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsAllCustomersWhenNoCriteriaIsProvided() throws Exception {
		when(customerRepo.findAll()).thenReturn(customerList);
		MvcResult mvcResult = mockedRequest.perform(get("/admin/rest/customers/")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenIdIsFound() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer));
		MvcResult mvcResult = mockedRequest.perform(get("/rest/customers/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.firstName").value("Bob"))
				.andExpect(jsonPath("$.lastName").value("Smith"))
				.andExpect(jsonPath("$.email").value("bob.smith@comcast.com"))
				.andExpect(jsonPath("$.phoneNumber").value("222-805-222"))
				.andExpect(jsonPath("$.username").value("user1"))
				.andExpect(jsonPath("$.password").value("p@ssword"))
				.andExpect(jsonPath("signUpDate").value(signUpDate.toString())).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
}
