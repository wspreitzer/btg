package com.btg.website.repository;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.btg.website.model.Faq;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

@ExtendWith(SpringExtension.class)
@SuppressWarnings("unchecked")
public class FaqRepositoryTest {

	@MockBean
	private FaqRepository faqRepo;
	
	private Faq faq, faq2, faq3, faq4;
	
	private List<Faq> repository;
	
	private List<Faq> results;
	
	@BeforeEach
	public void setup() {
		faqRepo = mock(FaqRepository.class);
		faq = new Faq("What color is the sun","The sun is yellow");
		faq2 = new Faq("What color is the sky","The sky is blue");
		faq3 = new Faq("What Color is money","Money is green");
		faq4 = new Faq("What Day of the week is it","It is Tuesday");
		repository = setupRepository(faq, faq2, faq3, faq4);
	}
	
	@Test
	public void returnsNoResultWhenIdIsNotFound() throws Exception {
		when(faqRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<Faq> emptyFaq = faqRepo.findById(-1L);
		assertThat(false, is(emptyFaq.isPresent()));
	}
	
	@Test
	public void returnsAllFaqsWhenNoSearchCriteriaIsProvided() throws Exception {
		when(faqRepo.findAll()).thenReturn(repository);
		List<Faq> returnedFaqes = (List<Faq>) faqRepo.findAll();
		assertThat(returnedFaqes.size(), is(4));
		assertThat(returnedFaqes, containsInAnyOrder(faq, faq2, faq3, faq4));
	}
	
	@Test
	public void returnsFaqWhenIdIsFound() throws Exception {
		when(faqRepo.findById(anyLong())).thenReturn(Optional.of(faq));
		Optional<Faq> foundFaq = faqRepo.findById(1L);
		assertThat(true, is(foundFaq.isPresent()));
		assertThat(foundFaq.get().getQuestion(), is("What color is the sun"));
		assertThat(foundFaq.get().getAnswer(), is("The sun is yellow"));
	}
	
	@Test
	public void savesFaqToRepositorySuccessfully() throws Exception {
		Faq faqToSave = new Faq("Bill","Clinton");
		when(faqRepo.save(any(Faq.class))).thenReturn(faqToSave);
		Faq newFaq = faqRepo.save(faqToSave);
		assertThat(newFaq.getQuestion(), is("Bill"));
		assertThat(newFaq.getAnswer(), is("Clinton"));
	}
	
	@Test
	public void savesMutipleFaqToRepositorySuccessfully() throws Exception {
		List<Faq> listOfFaqsToSave = new ArrayList<Faq>();
		Faq faqToSave = new Faq("New", "Faq");
		Faq faqToSave2 = new Faq("New2", "Faq2");
		
		listOfFaqsToSave.add(faqToSave);
		listOfFaqsToSave.add(faqToSave2);
		when(faqRepo.saveAll(anyCollection())).thenReturn(listOfFaqsToSave);
		List<Faq> savedFaqs = faqRepo.saveAll(listOfFaqsToSave);
		assertThat(savedFaqs.size(), is(2));
		assertThat(savedFaqs, containsInAnyOrder(faqToSave2, faqToSave));
	}
	
	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(faqRepo.count()).thenReturn((long) repository.size());
		long count = faqRepo.count();
		assertThat(count, is(4L));
	}

	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(faqRepo).deleteAll();
		faqRepo.deleteAll();
		verify(faqRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteFaqFromRepositoryById() throws Exception {
		when(faqRepo.findById(anyLong())).thenReturn(Optional.of(faq4));
		Faq foundFaq = faqRepo.findById(4L).get();
		doAnswer(invocation -> {
			repository.remove(3);
			return null;
		}).when(faqRepo).deleteById(anyLong());
		faqRepo.deleteById(4L);
		verify(faqRepo).deleteById(4L);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(foundFaq)));
	}
	
	@Test
	public void deleteProvidedCollectionOfFaqsFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(faqRepo).deleteAll(anyCollection());
		faqRepo.deleteAll(repository);
		verify(faqRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteFaqsFromRepositoryByGivenIds() throws Exception {
		when(faqRepo.findAllById(anyCollection())).thenReturn(setupRepository(faq, faq2));
		doAnswer(invocation -> {
			Iterable<Faq> faqesToDelete = faqRepo.findAllById(Arrays.asList(1L, 2L));
			faqesToDelete.forEach(aFaq -> repository.remove(aFaq));
			return null;
		}).when(faqRepo).deleteAllById(anyCollection());
		faqRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(faqRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(faq)));
		assertThat(repository, not(hasItem(faq2)));
	}
	
	@Test
	public void deleteProvidedFaqSuccessfully() throws Exception {
		when(faqRepo.findById(anyLong())).thenReturn(Optional.of(faq));
		Faq foundFaq = faqRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(faqRepo).delete(any(Faq.class));
		faqRepo.delete(foundFaq);
		verify(faqRepo).delete(foundFaq);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(faq)));
	}
	
	@Test
	public void returnsFaqWhenQuestionEquals() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(setupRepository(faq));
		results = faqRepo.findAll(new BtgSpecification<Faq> (new SearchCriteria("question", SearchOperation.EQUALITY, "What color is the sun")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(faq));
	}
	
	@Test
	public void returnsFaqWhenQuestionBeginsWith() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(setupRepository(faq2));
		results = faqRepo.findAll(new BtgSpecification<Faq>(new SearchCriteria("question", SearchOperation.STARTS_WITH, "What color is the Sk")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(faq2));
	}
	
	@Test
	public void returnsFaqWhenQuestionEndsWith() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(setupRepository(faq3));
		results = faqRepo.findAll(new BtgSpecification<Faq>(new SearchCriteria("question", SearchOperation.ENDS_WITH, "money")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(faq3));
	}
	
	@Test
	public void returnsFaqWhenQuestionContains() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(setupRepository(faq4));
		results = faqRepo.findAll(new BtgSpecification<Faq>(new SearchCriteria("question", SearchOperation.CONTAINS, "Day")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(faq4));
	}
	
	@Test
	public void returnsFaqWhenQuestionDoesntEqual() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(setupRepository(faq, faq2, faq3, faq4));
		results = faqRepo.findAll(new BtgSpecification<Faq>(new SearchCriteria("question", SearchOperation.NEGATION, "What month is it")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(faq, faq2, faq3, faq4));
	}
	
	@Test
	public void returnsFaqWhenAnswerEquals() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(setupRepository(faq));
		results = faqRepo.findAll(new BtgSpecification<Faq>(new SearchCriteria("answer", SearchOperation.EQUALITY, "The sun is yellow")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(faq));
	}
	
	@Test
	public void returnsFaqWhenAnswerBeginsWith() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(setupRepository(faq2));
		results = faqRepo.findAll(new BtgSpecification<Faq>(new SearchCriteria("answer", SearchOperation.STARTS_WITH, "The sky is")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(faq2));
	}
	
	@Test
	public void returnsFaqWhenAnswerEndsWith() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(setupRepository(faq3));
		results = faqRepo.findAll(new BtgSpecification<Faq>(new SearchCriteria("answer", SearchOperation.ENDS_WITH, "green")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(faq3));
	}
	
	@Test
	public void returnsFaqWhenAnswerContains() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(setupRepository(faq, faq2, faq3, faq4));
		results = faqRepo.findAll(new BtgSpecification<Faq>(new SearchCriteria("answer", SearchOperation.CONTAINS, "is")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(faq, faq2, faq3, faq4));
	}
	
	@Test
	public void returnsFaqWhenAnswerDoesntEqual() throws Exception {
		when(faqRepo.findAll(any(Specification.class))).thenReturn(setupRepository(faq, faq2, faq3, faq4));
		results = faqRepo.findAll(new BtgSpecification<Faq>(new SearchCriteria("answer", SearchOperation.NEGATION, "The month is May")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(faq, faq2, faq3, faq4));
	}

	
	private List<Faq> setupRepository(Faq...faqs) {
		List<Faq> faqList = new ArrayList<Faq>();
		for (Faq aFaq : faqs) {
			faqList.add(aFaq);
		}
		return faqList;
	}
	
	
}
