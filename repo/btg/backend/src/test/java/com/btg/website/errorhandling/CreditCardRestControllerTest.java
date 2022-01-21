package com.btg.website.errorhandling;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.List;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.btg.website.WebsiteApplication;
import com.btg.website.config.JacksonConfig;
import com.btg.website.model.CreditCard;
import com.btg.website.repository.CreditCardRepository;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonConfig.class})
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK, classes= {WebsiteApplication.class})
public class CreditCardRestControllerTest {
	
	@Autowired
	private MockMvc mockedRequest;
	
	@MockBean
	private CreditCardRepository creditCardRepo;
	

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	private TestUtils<CreditCard> creditCardUtils;
	private CreditCard card, card2, card3, card4;
	private List<CreditCard> creditCardList;
	
	@BeforeEach
	public void setup() {
		this.mockedRequest = webAppContextSetup(webApplicationContext).build();
		creditCardUtils = new TestUtils<CreditCard>();
		creditCardRepo = mock(CreditCardRepository.class);
		card = new CreditCard(null, "Visa", "4242424242424242", "12/25", "123");
		card2 = new CreditCard(null, "MasterCard", "5200828282828210", "12/25", "558");
		card3 = new CreditCard(null, "American Express", "354867813057915", "04/24", "7176");
		card4 = new CreditCard(null, "Discover", "6011245599887744", "07/24", "855");
		creditCardList = creditCardUtils.setupRepository(card, card2, card3, card4);
	}
 
	@Test
	public void should_Return_All_Customers_Credit_Cards() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardList);
		
		MvcResult mvcResult = mockedRequest
		.perform(get("/rest/creditcards/user1")).andReturn();
		System.out.println("Is this the json?");
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void should_be_able_to_handle_method_argument_not_valid_exception() throws Exception {
	}
	
	@Test
	public void should_be_able_to_handle_constraint_violation_exception() throws Exception {
		//mockedRequest.perform(post("/add"))
	}
	
	
}
