package com.btg.website.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.btg.website.WebsiteApplication;
import com.btg.website.config.JacksonConfig;
import com.btg.website.model.Account;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonConfig.class})
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK,classes= { WebsiteApplication.class })
public class AccountControllerTest {

	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@MockBean
	private AccountService accountServiceMock;
	
	private MockMvc mockMvc;
	private Account account;
	
	@BeforeEach
	public void setUp() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
		account = new Account(12345L, "SAVINGS", 5000.0);
	}
	
	@Test
	public void should_CreateAccount_When_ValidRequest() throws Exception {
		when(accountServiceMock.save(any(Account.class))).thenReturn(account);
		mockMvc.perform(post("/api/account")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountType\" : \"SAVINGS\", \"balance\" : \"5000.0\" }")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/api/account/12345"))
				.andExpect(jsonPath("$.accountId").value("12345"))
				.andExpect(jsonPath("$.accountType").value("SAVINGS"))
				.andExpect(jsonPath("$.balance").value(5000));
	}
	
	@Test
	public void should_GetAccount_When_ValidRequest() throws Exception {
		Account account = new Account(12345L, "SAVINGS", 5000.0);
		when(accountServiceMock.getById(12345L)).thenReturn(account);
		mockMvc.perform(get("/api/account/12345")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accountId").value(12345))
				.andExpect(jsonPath("$.accountType").value("SAVINGS"))
				.andExpect(jsonPath("$.balance").value(5000.0));
	}
	
	@Test
	public void should_Return404_When_AccountNotFound() throws Exception {
		when(accountServiceMock.getById(12345L)).thenReturn(null);
		mockMvc.perform(get("/api/account/12345")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}