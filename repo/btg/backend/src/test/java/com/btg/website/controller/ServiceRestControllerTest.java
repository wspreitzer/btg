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
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

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
import com.btg.website.model.Service;
import com.btg.website.repository.ServiceRepository;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonConfig.class})
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK, classes= {WebsiteApplication.class})
@SuppressWarnings("unchecked")
public class ServiceRestControllerTest {
	
	@MockBean private ServiceRepository serviceRepo;
	@InjectMocks private ServiceRestController controller;
	@Autowired private WebApplicationContext webApplicationContext;
	
	private MockMvc mockedRequest;
	private TestUtils<Service> serviceUtils;
	private Service service, service2, service3, service4;
	private List<Service> serviceList;
	
	@BeforeEach
	public void setup() {
		serviceUtils = new TestUtils<Service>();
		service = new Service("Website Development", "We will build your website", 7299.99);
		service2 = new Service("Customer Service Management System", "We will create a full featured CSMS for you", 7499.99);
		service3 = new Service("Social Media Platform", "We will create your great social media presence", 1499.95);
		service4 = new Service("Webisite Hosting", "We will provide and host a great domain for you", 499.95);
		serviceList = serviceUtils.setupRepository(service, service2, service3, service4);
		this.mockedRequest = webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void createsServiceWhenRequestIsValid() throws Exception {
		Service serviceToSave = new Service("Hosted website 3","Hosted website with 3 year contract", 7595.00);
		when(serviceRepo.save(any(Service.class))).thenReturn(serviceToSave);
		MvcResult mvcResult = mockedRequest
				.perform(post("/btg/admin/rest/service")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\" : \"Hosted website 3\", \"description\" : \"Hosted website with 3 year contract\", \"price\" : \"7595.00\"}")                                
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.serviceName").value("Hosted website 3"))
				.andExpect(jsonPath("$.description").value("Hosted website with 3 year contract"))
				.andExpect(jsonPath("$.price").value("7595.0")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returns404WhenIdIsNotFound() throws Exception {
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/service/0")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsAllServices() throws Exception {
		when(serviceRepo.findAll()).thenReturn(serviceList);
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/services/")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsServiceWhenIdIsFound() throws Exception {
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.of(service));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/service/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	/*
	@Test
	public void returnsServiceWhenServiceNameEquals() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "serviceName:Website Development")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsServiceWhenServiceNameBeginsWith() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "serviceName:Customer*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsServiceWhenServiceNameEndsWith() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "serviceName:*Platform")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsServiceWhenServiceNameContains() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service, service4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "serviceName:*Website*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsServiceWhenServiceNameDoesNotEqual() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service, service2, service3, service4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "serviceName!Video Game Development")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnServiceWhenDescriptionEquals() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "description:We will build your website")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnServiceWhenDescriptionBeginsWith() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service, service2, service3, service4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "description:We Will*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnServiceWhenDescriptionEndsWithd() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service2, service4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "description:*for you")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnServiceWhenDescriptionContains() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service2, service3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "description:*create*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnServiceWhenDescriptionDoesNotEqual() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service, service2, service4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "description!Social Media Platform")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	
	@Test
	public void returnServiceWhenPriceEquals() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "price:7299.99")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	
	@Test
	public void returnServiceWhenPriceBeginsWith() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "price:74*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	
	@Test
	public void returnServiceWhenPriceEndsWithd() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service3, service4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "price:*.95")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	
	@Test
	public void returnServiceWhenPriceContains() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "price:*49*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	
	@Test
	public void returnServiceWhenPriceDoesNotEqual() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service, service2, service3, service4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "price!*.95")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	
	@Test
	public void returnServiceWhenPriceIsLessThan() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service3, service4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchServices")
						.param("search", "price<1500.00")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	
	@Test
	public void returnServiceWhenPriceIsGreaterThan() throws Exception {
			when(serviceRepo.findAll(any(Specification.class))).thenReturn(serviceUtils.setupRepository(service3, service4));
			MvcResult mvcResult = mockedRequest
					.perform(get("/btg/rest/searchServices")
							.param("search", "price>1500.00")
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	*/
	@Test
	public void updatesServiceWhenRequestIsValid() throws Exception {
		service = new Service("Website Development", "We will build your website", 7299.99);
		when(serviceRepo.save(any(Service.class))).thenReturn(service);
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.of(service));
		MvcResult mvcResult = mockedRequest
				.perform(put("/btg/admin/rest/updateService/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"serviceName\" : \"Super Website Development\", \"description\" : \"We will build your super website\", \"price\" : \"8500.00\"}")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.serviceName").value("Super Website Development"))
				.andExpect(jsonPath("$.description").value("We will build your super website"))
				.andExpect(jsonPath("$.price").value(Double.valueOf("8500.00"))).andReturn();
		Service foundService = serviceRepo.findById(1L).get();
		assertThat(foundService.getServiceName(), is ("Super Website Development"));
		assertThat(foundService.getDescription(), is("We will build your super website"));
		assertThat(foundService.getPrice(), is(Double.valueOf("8500.00")));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesServiceNameWhenRequestIsValid2() throws Exception {
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.of(service));
		when(serviceRepo.save(any(Service.class))).thenReturn(service);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/admin/rest/service/1")
						.contentType("application/json-patch+json")
						.content("[{\"op\":\"replace\",\"path\":\"/serviceName\",\"value\":\"k76300\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		service.setServiceName("k76300");
		Service foundCustomer = serviceRepo.findById(3L).get();
		assertThat(foundCustomer.getServiceName(), is("k76300"));
		assertThat(foundCustomer.getDescription(), is(service.getDescription()));
		assertThat(foundCustomer.getPrice(), is(Double.valueOf(service.getPrice())));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesServiceNameWhenRequestIsValid() throws Exception {
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.of(service2));
		when(serviceRepo.save(any(Service.class))).thenReturn(service2);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/admin/rest/service/2")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/serviceName\", \"value\" : \"Super Website Development\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		service2.setServiceName("Super Website Development");
		Service foundService = serviceRepo.findById(2L).get();
		assertThat(foundService.getServiceName(), is("Super Website Development"));
		assertThat(foundService.getDescription(), is(service2.getDescription()));
		assertThat(foundService.getPrice(), is(service2.getPrice()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesServiceDescriptionWhenRequestIsValid() throws Exception {
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.of(service2));
		when(serviceRepo.save(any(Service.class))).thenReturn(service2);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/admin/rest/service/2")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/description\", \"value\" : \"We will build your super website\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		service2.setDescription("We will build your super website");
		Service foundService = serviceRepo.findById(2L).get();
		assertThat(foundService.getServiceName(), is(service2.getServiceName()));
		assertThat(foundService.getDescription(), is("We will build your super website"));
		assertThat(foundService.getPrice(), is(service2.getPrice()));
		System.out.println(mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void updatesServicePriceWhenRequestIsValid() throws Exception {
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.of(service2));
		when(serviceRepo.save(any(Service.class))).thenReturn(service2);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/admin/rest/service/2")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/price\", \"value\" : \"9500.00\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		service2.setPrice(9500.00);
		Service foundService = serviceRepo.findById(2L).get();
		assertThat(foundService.getServiceName(), is(service2.getServiceName()));
		assertThat(foundService.getDescription(), is(service2.getDescription()));
		assertThat(foundService.getPrice(), is(Double.valueOf("9500.00")));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void deletesServiceByIdWhenRequestIsValid() throws Exception {
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.of(service4));
		Service foundService = serviceRepo.findById(4L).get();
		doAnswer(invocation -> {
			serviceList.remove(3);
			return null;
		}).when(serviceRepo).deleteById(anyLong());
		mockedRequest.perform(delete("/btg/admin/rest/service/4")).andExpect(status().isNoContent());
		verify(serviceRepo).deleteById(4L);
		assertThat(serviceList.size(), is(3));
		assertThat(serviceList, not(hasItem(foundService)));
		
	}

	@Test
	public void deletesAllServicesWhenRequestIsValid() throws Exception {
		doAnswer(invocation -> {
			serviceList.clear();
			return null;
		}).when(serviceRepo).deleteAll();
		mockedRequest.perform(delete("/btg/admin/rest/services")).andExpect(status().isNoContent());
		verify(serviceRepo).deleteAll();
		assertThat(serviceList.size(), is(0));
	}
}