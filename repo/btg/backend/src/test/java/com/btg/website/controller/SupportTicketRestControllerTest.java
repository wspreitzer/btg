package com.btg.website.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.btg.website.model.Customer;
import com.btg.website.model.SupportTicket;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.SupportTicketRepository;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonConfig.class})
@Transactional
@SpringBootTest(webEnvironment=WebEnvironment.MOCK, classes = {WebsiteApplication.class})
@SuppressWarnings("unchecked")
public class SupportTicketRestControllerTest {

	@MockBean private SupportTicketRepository supportTicketRepo;
	@MockBean private CustomerRepository customerRepo;
	@InjectMocks SupportTicketRestController controller;
	@Autowired private WebApplicationContext webApplicationContext;
	
	private MockMvc mockedRequest;
	private TestUtils<SupportTicket> supportTicketUtils;
	private TestUtils<Customer> customerUtils;
	private SupportTicket ticket, ticket2, ticket3, ticket4;
	private List<SupportTicket> supportTicketList;
	private Customer customer;
	private Date createDate;
	
	@BeforeEach
	public void setup() {
		supportTicketUtils = new TestUtils<SupportTicket>();
		customerUtils = new TestUtils<Customer>();
		createDate = new Date(System.currentTimeMillis());
		ticket = new SupportTicket("", "", createDate, "", customer);
		ticket2 = new SupportTicket("", "", createDate, "", customer);
		ticket3 = new SupportTicket("", "", createDate, "", customer);
		ticket4 = new SupportTicket("", "", createDate, "", customer);
		supportTicketList = supportTicketUtils.setupRepository(ticket, ticket2, ticket3, ticket4);
		this.mockedRequest = webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void createsSupportTicketWhenRequestIsValid() throws Exception {
		SupportTicket supportTicketToSave = new SupportTicket("title", "description", createDate, "new", customer);
		when(supportTicketRepo.save(any(SupportTicket.class))).thenReturn(supportTicketToSave);
		MvcResult mvcResult = mockedRequest
				.perform(post("/btg/rest/supportTicket/")
						.contentType(MediaType.APPLICATION_JSON)
						.content("")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value(""))
				.andExpect(jsonPath("$.description").value(""))
				.andExpect(jsonPath("$.status").value(""))
				.andExpect(jsonPath("$.customer.firstName").value(""))
				.andExpect(jsonPath("$.customer.firstName").value(""))
				.andExpect(jsonPath("$.customer.firstName").value(""))
				.andExpect(jsonPath("$.customer.firstName").value(""))
				.andExpect(jsonPath("$.customer.firstName").value(""))
				.andExpect(jsonPath("$.customer.firstName").value("")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returns404WhenIdIsNotFound() throws Exception {
		when(supportTicketRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/supportTicket/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsSupportTicketWhenIdIsFound() throws Exception {
		when(supportTicketRepo.findById(anyLong())).thenReturn(Optional.of(ticket));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/supportTicket/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsAllCustomerTickets() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(customerUtils.setupRepository(new Customer("Bob", "Smith", "bob.smith@comcast.net", 
				"222-805-2222", "user1", "p@ssword")));
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(supportTicketUtils.setupRepository(ticket, ticket2, ticket3, ticket4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/supportTickets")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	
}