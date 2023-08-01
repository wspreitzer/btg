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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.context.WebApplicationContext;

import com.btg.website.model.CreditCard;
import com.btg.website.model.Customer;
import com.btg.website.repository.CreditCardRepository;
import com.btg.website.repository.CustomerRepository;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.CreditCardModelAssembler;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CreditCardRestController.class)
@SuppressWarnings("unchecked")
public class CreditCardRestControllerTest {
	
	
	@MockBean private CreditCardRepository creditCardRepo;
	
	@MockBean private CustomerRepository customerRepo;
	
	@MockBean private CreditCardModelAssembler assembler;
	
	@Autowired
	private MockMvc mockedRequest;
	private TestUtils<CreditCard> creditCardUtils;
	private CreditCard card, card2, card3, card4, card5;
	private List<CreditCard> creditCardList;
	
	@BeforeEach
	public void setup() {
		creditCardUtils = new TestUtils<CreditCard>();
		
		card = new CreditCard(null, "Visa", "4242424242424242", "12","25", "123");
		card2 = new CreditCard(null, "MasterCard", "5200828282828210", "12","25", "558");
		card3 = new CreditCard(null, "American Express", "354867813057915", "04","24", "7176");
		card4 = new CreditCard(null, "Discover", "6011245599887744", "07","24", "855");
		card5 = new CreditCard(null, "Visa", "4234567890123456","04","24","557");
		creditCardList = creditCardUtils.setupRepository(card, card2, card3, card4);

		List<Customer> customers = new ArrayList<Customer>();
		Customer customer = new Customer();
		customer.setSignupDate(new Date(System.currentTimeMillis()));
		customers.add(customer);
		when(customerRepo.findAll(any(BtgSpecification.class))).thenReturn(customers);
	}
	 
	@Test
	public void returnsAllCustomersCreditCards() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardList);
		MvcResult mvcResult = mockedRequest
		.perform(get("/btg/rest/creditCards/")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsCreditCardById() throws Exception {
		when(creditCardRepo.findById(1L)).thenReturn(Optional.of(card));
		MvcResult mvcResult = mockedRequest.perform(get("/btg/rest/creditCard/1")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("Visa"))
			.andExpect(jsonPath("$.number").value("4242424242424242"))
			.andExpect(jsonPath("$.exMon").value("12"))
			.andExpect(jsonPath("$.exYr").value("25"))
		.andExpect(jsonPath("$.cvv").value("123")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void createCreditCardWhenValidRequest() throws Exception {
		Customer customer = new Customer("Bob", "Smith", "bob.smith@comcast.net",  "312-781-1916", "user1", "P@ssword");
		customer.setId(12345L);
		customer.setSignupDate(new Date(System.currentTimeMillis()));
		CreditCard cardToSave = new CreditCard(customer, "Discover", "6013123456781234", "07", "22", "555");
		when(creditCardRepo.save(any(CreditCard.class))).thenReturn(cardToSave);
		mockedRequest.perform(post("/btg/rest/creditCard")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"customer\" : { \"firstName\" : \"Bob\", \"lastName\" : \"Smith\", \"billingAddress\" : {}, \"email\" : \"bob.smith@comcast.net\", \"phoneNumber\" : \"312-781-1916\", \"userName\" : \"user1\", \"password\" : \"P@ssword\", \"signupDate\" : \"2022-01-23\"}, \"type\" : \"Discover\",\"number\" : \"6013123456781234\",\"exMon\" : \"07\",\"exYr\" : \"22\",\"cvv\" : \"555\"}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "/btg/rest/creditCards/0"))
				.andExpect(jsonPath("$.id").value("0"))
				.andExpect(jsonPath("$.customer.firstName").value("Bob"))
				.andExpect(jsonPath("$.customer.lastName").value("Smith"))
				.andExpect(jsonPath("$.customer.email").value("bob.smith@comcast.net"))
				.andExpect(jsonPath("$.customer.phoneNumber").value("312-781-1916"))
				.andExpect(jsonPath("$.customer.userName").value("user1"))
				.andExpect(jsonPath("$.customer.password").value("P@ssword"))
				.andExpect(jsonPath("$.customer.signupDate").value(customer.getSignupDate().toString()))
				.andExpect(jsonPath("$.type").value("Discover"))
				.andExpect(jsonPath("$.number").value("6013123456781234"))
				.andExpect(jsonPath("$.exMon").value("07"))
				.andExpect(jsonPath("$.exYr").value("22"))
				.andExpect(jsonPath("$.cvv").value("555"));
	}

	/*
	 * @Test public void returnsCreditCardCountWhenValidRequest() throws Exception {
	 * when(creditCardRepo.findAll(any(Specification.class))).thenReturn(
	 * creditCardList); MvcResult mvcResult =
	 * mockedRequest.perform(get("/btg/rest/creditCards/count")
	 * .accept(MediaType.APPLICATION_JSON)) .andExpect(status().isOk()).andReturn();
	 * String[] cards = mvcResult.getResponse().getContentAsString().split("},");
	 * ObjectMapper mapper = new ObjectMapper(); Arrays.asList(cards).forEach(aCard
	 * -> { CreditCard myCard; try { myCard = mapper.readValue(aCard,
	 * CreditCard.class); creditCardList.add(myCard); } catch
	 * (JsonProcessingException e) { e.printStackTrace(); } });
	 * 
	 * assertThat(creditCardList.size(), is(4)); }
	 */

	@Test
	public void deleteCreditCardByIdWhenDeleteRequestIsValid() throws Exception {
		when(creditCardRepo.findById(anyLong())).thenReturn(Optional.of(card4));
		CreditCard foundCreditCard = creditCardRepo.findById(4L).get();
		doAnswer(invocation -> {
			creditCardList.remove(3);
			return null;
		}).when(creditCardRepo).deleteById(anyLong());
		mockedRequest.perform(delete("/btg/rest/creditCards/4")).andExpect(status().isNoContent());
		verify(creditCardRepo).deleteById(4L);
		assertThat(creditCardList.size(), is(3));
		assertThat(creditCardList, not(hasItem(foundCreditCard)));
	}
	
	@Test
	public void returnsCreditCardWhenTypeEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card, card5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "type:Visa")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsCreditCardWhenTypeBeginsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card, card5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "type:Vi*")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
						//.andExpect(jsonPath("$.type").value("Visa"))
						//.andExpect(jsonPath("$.number").value("4242424242424242"))
						//.andExpect(jsonPath("$.exMon").value("12"))
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardWhenTypeEndsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "type:*ard")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenTypeContains() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card2, card3, card4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "type:*er*")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenTypeDoesntEqual() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card, card2, card3, card4, card5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "type!Diners Club")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsCreditCardsWhenNumberEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "number:6011245599887744")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test 
	public void returnsCreditCardsWhenNumberBeginsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "number:3548")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenNumberEndsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "number:8210")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenNumberContains() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card, card4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "number:24")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenNumberDoesntEqual() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card, card2, card3, card4, card5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "number!1111111111111111")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenExipreMonthEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card, card2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "expireMon:12")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenExipreMonthBeginsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card3, card4, card5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "expireMon:0*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenExipreMonthEndsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card3, card5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "expireMon:*4")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenExipreMonthDoesntEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card3, card4, card5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "expireMon!12")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenExipreYrEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card, card2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "expireMon:25")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenExipreYrBeginsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card, card2, card3, card4, card5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "expireMon:2*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenExipreYrEndsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card3, card4, card5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "expireMon:*4")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenExipreYrDoesntEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card3, card4, card5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "expireMon!25")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnCreditCardsWhenCvvEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "cvv:855")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test 
	public void returnsCreditCardsWhenCvvBeginsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "cvv:71")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenCvvEndsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "cvv:58")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnCreditCardsWhenCvvContains() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card2, card4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "cvv:55")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsCreditCardsWhenCvvDoesntEqual() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(creditCardUtils.setupRepository(card, card2, card3, card4, card5));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "cvv!000")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returns404ErrorMessageWhenCreditCardNotFoundById() throws Exception{
		when(creditCardRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest
			.perform(get("/btg/rest/creditCard/55")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returns404ErrorMessageWhenCreditCardNotFoundBySpecification() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(new ArrayList<CreditCard>());
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCardsBySpecification/")
						.param("search", "type:DinersClub")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isNotFound()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returns404ErrorMessageWhenCustomerHasNoSavedCreditCards() throws Exception {
		when(creditCardRepo.findAll()).thenReturn(new ArrayList<CreditCard>());
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/creditCards/")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isNotFound()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

/*
	@Test
	public void shouldbeabletohandlemethodargumentnotvalidexception() throws Exception {
	}
	
	@Test
	public void shouldbeabletohandleconstraintviolationexception() throws Exception {
		//mockedRequest.perform(post("/add"))
	}
	*/	
}