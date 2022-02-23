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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.btg.website.WebsiteApplication;
import com.btg.website.config.JacksonConfig;
import com.btg.website.model.Faq;
import com.btg.website.repository.FaqRepository;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonConfig.class})
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK, classes= { WebsiteApplication.class })
@SuppressWarnings("unchecked")
public class FaqRestControllerTest {

	@MockBean FaqRepository faqRepo;
	@InjectMocks FaqRestController controller;
	@Autowired private WebApplicationContext webApplicationContext;
	
	private MockMvc mockedRequest;
	private TestUtils<Faq> faqUtils;
	private Faq faq, faq2, faq3, faq4;
	private List<Faq> faqList;
	
	@BeforeEach
	public void setup() {
		faqUtils = new TestUtils<Faq>();
		faq = new Faq("What color is the sun", "The sun is yellow");
		faq2 = new Faq("What color is the sky", "The sky is blue");
		faq3 = new Faq("What color is money", "Money is green");
		faq4 = new Faq("What Day of the week is it", "It is Tuesday");
		faqList = faqUtils.setupRepository(faq, faq2, faq3, faq4);
		this.mockedRequest = webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void returnsResourceNotFoundWhenIdIsNotFound() throws Exception {
		when(faqRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest.perform(get("/btg/rest/faqs/-1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsAllFaqsWhenNoCriteriaIsProvided() throws Exception {
		when(faqRepo.findAll()).thenReturn(faqList);
		MvcResult mvcResult = mockedRequest.perform(get("/btg/rest/faqs/")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsFaqByIdWhenIdIsFound() throws Exception {
		when(faqRepo.findById(1L)).thenReturn(Optional.of(faq2));
		MvcResult mvcResult = mockedRequest.perform(get("/btg/rest/faqs/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.question").value("What color is the sky"))
				.andExpect(jsonPath("$.answer").value("The sky is blue")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsFaqsWhenQuestionEquals() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(faqUtils.setupRepository(faq));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchfaqs/")
						.param("search", "question:What color is the sun")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsFaqsWhenQuestionBeginsWith() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(faqUtils.setupRepository(faq, faq2, faq3, faq4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchfaqs/")
						.param("search", "question:What")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsFaqsWhenQuestionEndsWith() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(faqUtils.setupRepository(faq2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchfaqs/")
						.param("search", "question:*blue")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsFaqsWhenQuestionContains() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(faqUtils.setupRepository(faq, faq2, faq3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchfaqs/")
						.param("search", "question:*color*")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsFaqsWhenQuestionDoesntEqual() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(faqUtils.setupRepository(faq, faq2, faq3, faq4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchfaqs/")
						.param("search", "question!What time is it")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsFaqsWhenAnswerEquals() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(faqUtils.setupRepository(faq));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchfaqs/")
						.param("search", "answer:The sun is yellow")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsFaqsWhenAnswerBeginsWith() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(faqUtils.setupRepository(faq, faq2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchfaqs/")
						.param("search", "answer:The*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsFaqsWhenAnswerEndsWith() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(faqUtils.setupRepository(faq3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchfaqs/")
						.param("search", "answer:*green")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsFaqsWhenAnswerContains() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(faqUtils.setupRepository(faq));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchfaqs/")
						.param("search", "answer:*is*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsFaqsWhenAnswerDoesntEqual() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(faqUtils.setupRepository(faq, faq2, faq3, faq4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchfaqs/")
						.param("search", "answer!The time is 5pm")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void createsFaqWhenRequestIsValid() throws Exception {
		Faq faqToSave = new Faq("How much does a website cost", "A website costs 7500");
		when(faqRepo.save(any(Faq.class))).thenReturn(faqToSave);
		MvcResult mvcResult = mockedRequest.perform(post("/btg/admin/rest/faqs")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"question\" : \"How much does a website cost\", \"answer\" : \"A website costs 7500\"}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				//.andExpect(header().string("Location", "http://localhost/btg/rest/faqs/0"))
				.andExpect(jsonPath("$.question").value("How much does a website cost"))
				.andExpect(jsonPath("$.answer").value("A website costs 7500")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void deleteFaqByIdWhenRequestIsValid() throws Exception {
		when(faqRepo.findById(anyLong())).thenReturn(Optional.of(faq4));
		Faq foundFaq = faqRepo.findById(4L).get();
		doAnswer(invocation -> {
			faqList.remove(3);
			return null;
		}).when(faqRepo).deleteById(anyLong());
		mockedRequest.perform(delete("/btg/admin/rest/faqs/4"))
				.andExpect(status().isNoContent());
		verify(faqRepo).deleteById(4L);
		assertThat(faqList.size(), is(3));
		assertThat(faqList, not(hasItem(foundFaq)));
	}

	@Test
	public void updatesFaqQuestionWhenRequestIsValid() throws Exception {
		when(faqRepo.findById(anyLong())).thenReturn(Optional.of(faq4));
		Faq foundFaq = faqRepo.findById(4L).get();
		Faq updatedFaq = new Faq();
		updatedFaq.setId(foundFaq.getId());
		updatedFaq.setQuestion("What day of the week is it?");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("field", "question");
		params.add("update", "What day of the week is it?");
		when(faqRepo.save(any(Faq.class))).thenReturn(updatedFaq);
		MvcResult mvcResult = mockedRequest.perform(patch("/btg/admin/rest/faqs/4/")
				.params(params)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath(".question").value("What day of the week is it?")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesFaqAnswerWhenRequestIsValid() throws Exception {
		when(faqRepo.findById(anyLong())).thenReturn(Optional.of(faq4));
		Faq foundFaq = faqRepo.findById(4L).get();
		Faq updatedFaq = new Faq();
		updatedFaq.setId(foundFaq.getId());
		updatedFaq.setAnswer("It is Wednesday");
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("field", "answer");
		params.add("update", "It is Wednesday");
		when(faqRepo.save(any(Faq.class))).thenReturn(updatedFaq);
		MvcResult mvcResult = mockedRequest.perform(patch("/btg/admin/rest/faqs/4")
				.params(params)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.answer").value("It is Wednesday")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	
	
	@Test
	public void updatesFaqWhenRequestIsValid() throws Exception {
		Faq updateFaq = new Faq();
		updateFaq.setId(updateFaq.getId());
		updateFaq.setQuestion("What color is the sky?");
		updateFaq.setAnswer("The sky is blue!");
		when(faqRepo.save(any(Faq.class))).thenReturn(updateFaq);
		MvcResult mvcResult = mockedRequest.perform(put("/btg/admin/rest/faqs/")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"question\" : \"What color is the sky?\", \"answer\" : \"The sky is blue!\"}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.question").value("What color is the sky?"))
				.andExpect(jsonPath("$.answer").value("The sky is blue!")).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
}