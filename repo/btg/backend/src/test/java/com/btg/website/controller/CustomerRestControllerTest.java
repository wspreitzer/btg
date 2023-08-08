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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.btg.website.model.Customer;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.util.CustomerModelAssembler;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerRestController.class)
@SuppressWarnings("unchecked")
public class CustomerRestControllerTest {

	@MockBean private CustomerRepository customerRepo;
	@MockBean private CustomerModelAssembler modelAssembler;
	@MockBean private BtgSpecificationBuilder<Customer> builder;
	@Autowired private MockMvc mockedRequest;
	
	private Customer customer, customer2, customer3, customer4;
	
	private List<Customer> customerList;
	
	private Date signUpDate;
	
	private TestUtils<Customer> customerUtils;
	
	@BeforeEach
	public void setup() {

		customer = new Customer("Bob", "Smith", "bob.smith@comcast.com", 
				"222-805-2222", "user1", "p@ssword");
		customer2 = new Customer("John", "smythe", "john.smythe@comcast.net", 
				"312-781-1916", "user2", "Pword");
		customer3 = new Customer("Jon", "Doe", "jon.doe@company.com", 
				"312-693-0103", "jdoe", "P@ssw0rd");
		customer4 = new Customer("Tom", "Garcia","tgarcia@company2.net", 
				"773-805-3203", "tgarcia", "!P@ssW0rd");
		customerUtils = new TestUtils<Customer>();
		
		signUpDate = new Date(System.currentTimeMillis());
		
		customer.setSignupDate(signUpDate);
		customer2.setSignupDate(signUpDate);
		customer3.setSignupDate(signUpDate);
		customer4.setSignupDate(signUpDate);
		
		customer.setId(1L);
		customer2.setId(2L);
		customer3.setId(3L);
		customer4.setId(4L);
		
		customerList = customerUtils.setupRepository(customer, customer2, customer3, customer4);
	}

	@Test
	public void returns404WhenCustomerIsNotFoundById() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest.perform(get("/btg/rest/customers/0")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsAllCustomersWhenNoCriteriaIsProvided() throws Exception {
		when(customerRepo.findAll()).thenReturn(customerList);
		MvcResult mvcResult = mockedRequest.perform(get("/btg/admin/rest/customers/")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenIdIsFound() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer));
		MvcResult mvcResult = mockedRequest.perform(get("/btg/rest/customers/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value("Bob"))
				.andExpect(jsonPath("$.lastName").value("Smith"))
				.andExpect(jsonPath("$.email").value("bob.smith@comcast.com"))
				.andExpect(jsonPath("$.phoneNumber").value("222-805-2222"))
				.andExpect(jsonPath("$.username").value("user1"))
				.andExpect(jsonPath("$.password").value("p@ssword"))
				.andExpect(jsonPath("$.signupDate").value(signUpDate.toString())).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenFirstNameEquals() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "firstName:Bob")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenFirstNameBeginsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer2, customer3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "firstName:Jo*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenFirstEndsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "firstName:*ob")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenFirstNameContains() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "firstName:*To*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenFirstNameDoesNotEqual() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "firstName!Robert")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenLastNameEqauls() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "lastName:Garcia")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	
	  @Test 
	  public void returnsCustomerWhenLastNameEqauls2() throws Exception {
		  when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer)); 
		  MvcResult mvcResult = mockedRequest
				  .perform(get("/btg/rest/customerSearch/") 
						  .param("search", "lastName:Smith")
						  .accept(MediaType.APPLICATION_JSON)) 
				  .andExpect(status().isOk()).andReturn();
	  System.out.println(mvcResult.getResponse().getContentAsString()); }
	 
	
	
	@Test
	public void returnsCustomerWhenLastNameBeginsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "lastName:Do*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsCustomerWhenLastNameEndsWith2() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "lastName:*ith")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenLastNameContains() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer, customer2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "lastName:*th*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void returnsCustomerWhenLastNameDoesNotEqaul() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer2, customer3, customer4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "lastName!Smith")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenEmailEquals() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "email:bob.smith@comcast.com")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenEmailBeginsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer2, customer3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "email:jo*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenEmailEndsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer2, customer4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "email:*.net")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenEmailContains() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer3, customer4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "email:*@company*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsCustomerWhenEmailDoesNotEqual() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer2, customer3, customer4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "email!bob.smith@comcast.com")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void returnsCustomerWhenPhoneNumberEquals() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "phoneNumber:222-805-2222")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	
	@Test
	public void returnsCustomerWhenPhoneNumberBeginsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer2, customer3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "phoneNumber:312*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenPhoneNumberEndsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer3, customer4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "phoneNumber:*03")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsCustomerWhenPhoneNumberContains() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer, customer2, customer3, customer4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "phoneNumber:*-*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCustomerWhenPhoneNumberDoesNotEqual() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(customer2, customer3, customer4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/customerSearch/")
						.param("search", "phoneNumber!222-805-2222")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void createsCustomerWhenRequestIsValid() throws Exception {
		Customer customerToSave = new Customer("Patrick", "Kane", 
				"pkane@chicagoblackhawks.nhl.com", "312-258-3696", "pkane88", "p@ssw0rd");
		customerToSave.setSignupDate(signUpDate);
		customerToSave.setId(1L);
		when(customerRepo.save(any(Customer.class))).thenReturn(customerToSave);
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customerToSave));
		MvcResult mvcResult = mockedRequest.perform(post("/btg/rest/customer/")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\" : \"Patrick\", \"lastName\" : \"Kane\", \"billingAddress\" : {}, \"shippingAddress\" : {}, \"company\" : {},\"email\" : \"pkane@chicagoblackhawks.nhl.com\",\"phoneNumber\" : \"312-258-3696\", \"userName\" : \"pkane88\",\"password\" : \"p@ssw0rd\", \"signupDate\" : \"" + signUpDate + "\"}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		Customer foundCustomer = customerRepo.findById(1L).get();
		assertThat(foundCustomer.getFirstName(), is("Patrick"));
		assertThat(foundCustomer.getLastName(), is("Kane"));
		assertThat(foundCustomer.getEmail(), is("pkane@chicagoblackhawks.nhl.com"));
		assertThat(foundCustomer.getPhoneNumber(), is("312-258-3696"));
		assertThat(foundCustomer.getUsername(), is("pkane88"));
		assertThat(foundCustomer.getPassword(), is("p@ssw0rd"));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesCustomerByIdWhenRequestIsValid() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer));
		when(customerRepo.save(any(Customer.class))).thenReturn(customer);
		MvcResult mvcResult = mockedRequest
				.perform(put("/btg/rest/customer/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"firstName\" : \"Patrick\", \"lastName\" : \"Kane\", \"billingAddress\" : {}, \"shippingAddress\" : {}, \"company\" : {},\"email\" : \"pkane@chicagoblackhawks.nhl.com\",\"phoneNumber\" : \"312-258-3696\", \"userName\" : \"pkane88\",\"password\" : \"P@ssw0rd\", \"signupDate\" : \"" + signUpDate + "\"}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
			Customer foundCustomer = customerRepo.findById(1L).get();
			assertThat(foundCustomer.getFirstName(), is("Patrick"));
			assertThat(foundCustomer.getLastName(), is("Kane"));
			assertThat(foundCustomer.getEmail(), is("pkane@chicagoblackhawks.nhl.com"));
			assertThat(foundCustomer.getPhoneNumber(), is("312-258-3696"));
			assertThat(foundCustomer.getUsername(), is("pkane88"));
			assertThat(foundCustomer.getPassword(), is("P@ssw0rd"));
		System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	
	@Test
	public void updatesCustomerFirstNameWhenRequestIsValid() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer3));
		when(customerRepo.save(any(Customer.class))).thenReturn(customer3);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/customer/3")
						.contentType("application/json-patch+json")
						.content("[{\"op\":\"replace\",\"path\":\"/firstName\",\"value\":\"Billy Bob\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		customer3.setFirstName("Billy Bob");
		Customer foundCustomer = customerRepo.findById(3L).get();
		assertThat(foundCustomer.getFirstName(), is("Billy Bob"));
		assertThat(foundCustomer.getLastName(), is(customer3.getLastName()));
		assertThat(foundCustomer.getEmail(), is(customer3.getEmail()));
		assertThat(foundCustomer.getPhoneNumber(), is(customer3.getPhoneNumber()));
		assertThat(foundCustomer.getUsername(), is(customer3.getUsername()));
		assertThat(foundCustomer.getPassword(), is(customer3.getPassword()));
		assertThat(foundCustomer.getSignupDate(), is(customer3.getSignupDate()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void updatesCustomerLastNameWhenRequestIsValid() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer3));
		when(customerRepo.save(any(Customer.class))).thenReturn(customer3);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/customer/3")
				.contentType("application/json-patch+json")
				.content("[{\"op\":\"replace\",\"path\":\"/lastName\",\"value\":\"Towes\"}]")
				.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		customer3.setLastName("Towes");
		Customer foundCustomer = customerRepo.findById(3L).get();
		assertThat(foundCustomer.getFirstName(), is(customer3.getFirstName()));
		assertThat(foundCustomer.getLastName(), is("Towes"));
		assertThat(foundCustomer.getEmail(), is(customer3.getEmail()));
		assertThat(foundCustomer.getPhoneNumber(), is(customer3.getPhoneNumber()));
		assertThat(foundCustomer.getUsername(), is(customer3.getUsername()));
		assertThat(foundCustomer.getPassword(), is(customer3.getPassword()));
		assertThat(foundCustomer.getSignupDate(), is(customer3.getSignupDate()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void updatesCustomerEmailWhenRequestIsValid() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer3));
		when(customerRepo.save(any(Customer.class))).thenReturn(customer3);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/customer/3")
						.contentType("application/json-patch+json")
						.content("[{\"op\":\"replace\",\"path\":\"/email\",\"value\":\"email@email.com\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		customer3.setEmail("email@email.com");
		Customer foundCustomer = customerRepo.findById(3L).get();
		assertThat(foundCustomer.getFirstName(), is(customer3.getFirstName()));
		assertThat(foundCustomer.getLastName(), is(customer3.getLastName()));
		assertThat(foundCustomer.getEmail(), is("email@email.com"));
		assertThat(foundCustomer.getPhoneNumber(), is(customer3.getPhoneNumber()));
		assertThat(foundCustomer.getUsername(), is(customer3.getUsername()));
		assertThat(foundCustomer.getPassword(), is(customer3.getPassword()));
		assertThat(foundCustomer.getSignupDate(), is(customer3.getSignupDate()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesCustomerPhoneNumberWhenRequestIsValid() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer3));
		when(customerRepo.save(any(Customer.class))).thenReturn(customer3);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/customer/3")
						.contentType("application/json-patch+json")
						.content("[{\"op\":\"replace\",\"path\":\"/phoneNumber\",\"value\":\"8005882300\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		customer3.setPhoneNumber("8005882300");
		Customer foundCustomer = customerRepo.findById(3L).get();
		assertThat(foundCustomer.getFirstName(), is(customer3.getFirstName()));
		assertThat(foundCustomer.getLastName(), is(customer3.getLastName()));
		assertThat(foundCustomer.getEmail(), is(customer3.getEmail()));
		assertThat(foundCustomer.getPhoneNumber(), is("8005882300"));
		assertThat(foundCustomer.getUsername(), is(customer3.getUsername()));
		assertThat(foundCustomer.getPassword(), is(customer3.getPassword()));
		assertThat(foundCustomer.getSignupDate(), is(customer3.getSignupDate()));
		System.out.println(mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void updatesCustomerUserNameWhenRequestIsValid() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer3));
		when(customerRepo.save(any(Customer.class))).thenReturn(customer3);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/customer/3")
						.contentType("application/json-patch+json")
						.content("[{\"op\":\"replace\",\"path\":\"/userName\",\"value\":\"userName55\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		customer3.setUsername("userName55");
		Customer foundCustomer = customerRepo.findById(3L).get();
		assertThat(foundCustomer.getFirstName(), is(customer3.getFirstName()));
		assertThat(foundCustomer.getLastName(), is(customer3.getLastName()));
		assertThat(foundCustomer.getEmail(), is(customer3.getEmail()));
		assertThat(foundCustomer.getPhoneNumber(), is(customer3.getPhoneNumber()));
		assertThat(foundCustomer.getUsername(), is("userName55"));
		assertThat(foundCustomer.getPassword(), is(customer3.getPassword()));
		assertThat(foundCustomer.getSignupDate(), is(customer3.getSignupDate()));
		System.out.println(mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void updatesCustomerPasswordWhenRequestIsValid() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer3));
		when(customerRepo.save(any(Customer.class))).thenReturn(customer3);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/customer/3")
						.contentType("application/json-patch+json")
						.content("[{\"op\":\"replace\",\"path\":\"/password\",\"value\":\"k76300\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		customer3.setPassword("k76300");
		Customer foundCustomer = customerRepo.findById(3L).get();
		assertThat(foundCustomer.getFirstName(), is(customer3.getFirstName()));
		assertThat(foundCustomer.getLastName(), is(customer3.getLastName()));
		assertThat(foundCustomer.getEmail(), is(customer3.getEmail()));
		assertThat(foundCustomer.getPhoneNumber(), is(customer3.getPhoneNumber()));
		assertThat(foundCustomer.getUsername(), is(customer3.getUsername()));
		assertThat(foundCustomer.getPassword(), is("k76300"));
		assertThat(foundCustomer.getSignupDate(), is(customer3.getSignupDate()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void deletesCustomerByIdWhenRequestIsValid() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer));
		Customer foundCustomer = customerRepo.findById(1L).get();
		doAnswer(invocation -> {
			customerList.remove(0);
			return null;
		}).when(customerRepo).deleteById(anyLong());
		mockedRequest.perform(delete("/btg/rest/customer/1")).andExpect(status().isNoContent());
		verify(customerRepo).deleteById(1L);
		assertThat(customerList.size(), is(3));
		assertThat(customerList, not(hasItem(foundCustomer)));
	}
	
	@Test
	public void deletesMultipleCustomersWhenRequestIsValid() throws Exception {
		doAnswer(invocation -> {
			customerList.clear();
			return null;
		}).when(customerRepo).deleteAll();
		mockedRequest.perform(delete("/btg/admin/rest/customers/")).andExpect(status().isNoContent());
		verify(customerRepo).deleteAll();
		assertThat(customerList.size(), is(0));
	}
}