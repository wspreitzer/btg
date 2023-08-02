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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.btg.website.model.Customer;
import com.btg.website.model.SupportTicket;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.SupportTicketRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.util.SupportTicketModelAssembler;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SupportTicketRestController.class)
@SuppressWarnings("unchecked")
public class SupportTicketRestControllerTest {

	@MockBean private SupportTicketRepository supportTicketRepo;
	@MockBean private CustomerRepository customerRepo;
	@MockBean private SupportTicketModelAssembler assembler;
	@MockBean private BtgSpecificationBuilder<SupportTicket> builder;
	@Autowired private MockMvc mockedRequest;
	
	private TestUtils<SupportTicket> supportTicketUtils;
	private TestUtils<Customer> customerUtils;
	private SupportTicket ticket, ticket2, ticket3, ticket4;
	private List<SupportTicket> supportTicketList;
	private Customer customer;
	private Date createDate;
	
	@BeforeEach
	public void setup() {
		supportTicketUtils = new TestUtils<SupportTicket>();
		customer = new Customer("Bob", "Smith", "bob.smith@comcast.net", 
				"222-805-2222", "user1", "p@ssword");
		customerUtils = new TestUtils<Customer>();
		createDate = new Date(System.currentTimeMillis());
		ticket = new SupportTicket("User dashboard is down", "I am unable to access my dashboard", createDate, "new", customer);
		ticket2 = new SupportTicket("Website is down", "Website is down", createDate, "in progress", customer);
		ticket3 = new SupportTicket("Database is down", "Database is down", createDate, "resolved", customer);
		ticket4 = new SupportTicket("Emails are coming back as undeliverable", "All emails sent through my website are coming back as undeliverable", createDate, "closed", customer);
		supportTicketList = supportTicketUtils.setupRepository(ticket, ticket2, ticket3, ticket4);
		when(assembler.toModel(any(SupportTicket.class))).thenCallRealMethod();
	}
	
	@Test
	public void createsSupportTicketWhenRequestIsValid() throws Exception {
		SupportTicket supportTicketToSave = new SupportTicket("Website is down", "My website is down.  All connections are being refused", createDate, "new", customer);
		when(supportTicketRepo.save(any(SupportTicket.class))).thenReturn(supportTicketToSave);
		MvcResult mvcResult = mockedRequest
				.perform(post("/btg/rest/supportTicket/")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\" : \"Website is down\", \"description\" : \"My website is down.  All connections are being refused\", \"creationDate\" : \"" +createDate + "\", \"status\" : \"new\", \"customer\" : {\"firstName\" : \"Bob\", \"lastName\" : \"Smith\", \"email\" : \"bob.smith@comcast.net\", \"phoneNumber\" : \"222-805-2222\", \"userName\" : \"user1\", \"password\" : \"p@ssword\" }}")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value("Website is down"))
				.andExpect(jsonPath("$.description").value("My website is down.  All connections are being refused"))
				.andExpect(jsonPath("$.status").value("new"))
				.andExpect(jsonPath("$.customer.firstName").value("Bob"))
				.andExpect(jsonPath("$.customer.lastName").value("Smith"))
				.andExpect(jsonPath("$.customer.email").value("bob.smith@comcast.net"))
				.andExpect(jsonPath("$.customer.phoneNumber").value("222-805-2222"))
				.andExpect(jsonPath("$.customer.userName").value("user1"))
				.andExpect(jsonPath("$.customer.password").value("p@ssword")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returns404WhenIdIsNotFound() throws Exception {
		when(supportTicketRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/supportTicket/154")
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
	
	@Test
	public void updatesSupportTicketTitleWhenRequestIsValid() throws Exception {
		when(supportTicketRepo.findById(anyLong())).thenReturn(Optional.of(ticket2));
		when(supportTicketRepo.save(any(SupportTicket.class))).thenReturn(ticket2);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/updateSupportTicket/2")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/title\", \"value\" : \"Updated ticket title\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		ticket2.setTitle("Updated ticket title");
		SupportTicket foundTicket = supportTicketRepo.findById(2L).get();
		assertThat(foundTicket.getTitle(), is("Updated ticket title"));
		assertThat(foundTicket.getDescription(), is("Website is down"));
		assertThat(foundTicket.getStatus(), is("in progress")); 
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesSupportTicketDescriptionWhenRequestIsValid() throws Exception {
		when(supportTicketRepo.findById(anyLong())).thenReturn(Optional.of(ticket2));
		when(supportTicketRepo.save(any(SupportTicket.class))).thenReturn(ticket2);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/updateSupportTicket/2")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/description\", \"value\" : \"Updated ticket description\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		ticket2.setDescription("Updated ticket description");
		SupportTicket foundTicket = supportTicketRepo.findById(2L).get();
		assertThat(foundTicket.getTitle(), is("Website is down"));
		assertThat(foundTicket.getDescription(), is("Updated ticket description"));
		assertThat(foundTicket.getStatus(), is("in progress")); 
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesSupportTicketStatusWhenRequestIsValid() throws Exception {
		when(supportTicketRepo.findById(anyLong())).thenReturn(Optional.of(ticket2));
		when(supportTicketRepo.save(any(SupportTicket.class))).thenReturn(ticket2);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/updateSupportTicket/2")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/description\", \"value\" : \"closed\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		ticket2.setStatus("closed");
		SupportTicket foundTicket = supportTicketRepo.findById(2L).get();
		assertThat(foundTicket.getTitle(), is("Website is down"));
		assertThat(foundTicket.getDescription(), is("Website is down"));
		assertThat(foundTicket.getStatus(), is("closed"));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void deletesSupportTicketWhenRequestIsValid() throws Exception {
		when(supportTicketRepo.findById(anyLong())).thenReturn(Optional.of(ticket3));
		SupportTicket foundTicket = supportTicketRepo.findById(3L).get();
		doAnswer(invocation -> {
			supportTicketList.remove(2);
			return null;
		}).when(supportTicketRepo).deleteById(anyLong());
		mockedRequest.perform(delete("/btg/rest/supportTicket/3"));
		verify(supportTicketRepo).deleteById(3L);
		assertThat(supportTicketList.size(), is(3));
		assertThat(supportTicketList, not(hasItem(foundTicket)));
	}
}