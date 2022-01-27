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

import com.btg.website.model.CreditCard;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

@SuppressWarnings("unchecked")
@ExtendWith(SpringExtension.class)
public class CreditCardRepositoryTest {
	
	@MockBean
	private CreditCardRepository creditCardRepo;

	private CreditCard creditCard, creditCard2, creditCard3, creditCard4;
	private List<CreditCard> repository;
	private List<CreditCard> results;
	
	@BeforeEach
	public void setup() {
		creditCardRepo = mock(CreditCardRepository.class);
		creditCard = new CreditCard(null, "Visa", "4242424242424242", "12","25", "123");
		creditCard2 = new CreditCard(null, "Master Card", "5200828282828210", "12","25", "558");
		creditCard3 = new CreditCard(null, "American Express", "354867813057915", "04","24", "7176");
		creditCard4 = new CreditCard(null, "Discover", "6011245599887744", "07","24", "855");
		repository = setupRepository(creditCard, creditCard2, creditCard3, creditCard4);
	}
	
	@Test
	public void returnsNoResultsWhenIdIsNotFound() throws Exception {
		when(creditCardRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<CreditCard> emptyCreditCard = creditCardRepo.findById(1L);
		assertThat(false, is(emptyCreditCard.isPresent()));
	}
	@Test
	public void returnsAllCreditCardsWhenNoSearchCriteriaisProvided() throws Exception {
		when(creditCardRepo.findAll()).thenReturn(repository);
		List<CreditCard> returnedCreditCards = creditCardRepo.findAll();
		assertThat(returnedCreditCards, containsInAnyOrder(creditCard, creditCard2, creditCard3, creditCard4));
	}
	@Test
	public void returnsCreditCardWhenIdIsFound() throws Exception {
		when(creditCardRepo.findById(anyLong())).thenReturn(Optional.of(creditCard));
		Optional<CreditCard> foundCreditCard = creditCardRepo.findById(1L);
		assertThat(true, is(foundCreditCard.isPresent()));
		assertThat(foundCreditCard.get().getExMon(), is("12"));
		assertThat(foundCreditCard.get().getExYr(), is("25"));
		assertThat(foundCreditCard.get().getCvv(), is("123"));
		assertThat(foundCreditCard.get().getType(), is("Visa"));
		assertThat(foundCreditCard.get().getNumber(), is("4242424242424242"));
	}
	
	@Test
	public void savesCreditCardToRepositorySuccessfully() throws Exception {
		CreditCard creditCardToSave = new CreditCard(null,"Master Card","5555555555554444", "12","23", "069" );
		when(creditCardRepo.save(any(CreditCard.class))).thenReturn(creditCardToSave);
		CreditCard newCreditCard = creditCardRepo.save(creditCardToSave);
		assertThat(newCreditCard.getType(), is("Master Card"));
		assertThat(newCreditCard.getNumber(), is("5555555555554444"));
		assertThat(newCreditCard.getExMon(), is("12"));
		assertThat(newCreditCard.getExYr(), is("23"));
		assertThat(newCreditCard.getCvv(), is("069"));
	}
	
	@Test
	public void savesMutipleCreditCardToRepositorySuccessfully() throws Exception {
		List<CreditCard> listOfCreditCardsToSave = new ArrayList<CreditCard>();
		CreditCard creditCardToSave = new CreditCard(null,"Discover", "6011000990139424", "07","25", "069");
		CreditCard creditCardToSave2 = new CreditCard(null, "American Express", "371449635398431", "05","25", "7176");
		
		listOfCreditCardsToSave.add(creditCardToSave);
		listOfCreditCardsToSave.add(creditCardToSave2);
		when(creditCardRepo.saveAll(anyCollection())).thenReturn(listOfCreditCardsToSave);
		List<CreditCard> savedCreditCards = creditCardRepo.saveAll(listOfCreditCardsToSave);
		assertThat(savedCreditCards.size(), is(2));
		assertThat(savedCreditCards, containsInAnyOrder(creditCardToSave2, creditCardToSave));
	}
	
	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(creditCardRepo.count()).thenReturn((long) repository.size());
		long count = creditCardRepo.count();
		assertThat(count, is(4L));
	}
	
	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(creditCardRepo).deleteAll();
		creditCardRepo.deleteAll();
		verify(creditCardRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteCreditCardFromRepositoryById() throws Exception {
		when(creditCardRepo.findById(anyLong())).thenReturn(Optional.of(creditCard4));
		CreditCard foundCreditCard = creditCardRepo.findById(4L).get();
		doAnswer(invocation -> {
			repository.remove(3);
			return null;
		}).when(creditCardRepo).deleteById(anyLong());
		creditCardRepo.deleteById(4L);
		verify(creditCardRepo).deleteById(4L);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(foundCreditCard)));
	}
	
	@Test
	public void deleteProvidedCollectionOfCreditCardsFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(creditCardRepo).deleteAll(anyCollection());
		creditCardRepo.deleteAll(repository);
		verify(creditCardRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteCreditCardsFromRepositoryByGivenIds() throws Exception {
		when(creditCardRepo.findAllById(anyCollection())).thenReturn(setupRepository(creditCard, creditCard2));
		doAnswer(invocation -> {
			Iterable<CreditCard> creditCardesToDelete = creditCardRepo.findAllById(Arrays.asList(1L, 2L));
			creditCardesToDelete.forEach(anAddress -> repository.remove(anAddress));
			return null;
		}).when(creditCardRepo).deleteAllById(anyCollection());
		creditCardRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(creditCardRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(creditCard)));
		assertThat(repository, not(hasItem(creditCard2)));
	}
	
	@Test
	public void deleteProvidedCreditCardSuccessfully() throws Exception {
		when(creditCardRepo.findById(anyLong())).thenReturn(Optional.of(creditCard));
		CreditCard foundCreditCard = creditCardRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(creditCardRepo).delete(any(CreditCard.class));
		creditCardRepo.delete(foundCreditCard);
		verify(creditCardRepo).delete(foundCreditCard);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(creditCard)));
	}
	@Test
	public void returnsCreditCardWhenTypeEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("type", SearchOperation.EQUALITY, "Visa")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(creditCard));
	}
	@Test
	public void returnsCreditCardWhenTypeBeginsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard3));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("type", SearchOperation.STARTS_WITH, "Exp")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(creditCard3));
	}

	@Test
	public void returnsCreditCardWhenTypeEndWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard2));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("type", SearchOperation.ENDS_WITH, "ard")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(creditCard2));
	}

	@Test
	public void returnsCreditCardWhenTypeContains() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard2, creditCard3, creditCard4));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("type", SearchOperation.CONTAINS, "er")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(creditCard2, creditCard3, creditCard4));
	}

	@Test
	public void returnsCreditCardWhenTypeDoesntEqual() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard, creditCard2, creditCard3, creditCard4));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("type", SearchOperation.NEGATION, "Diners Club")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(creditCard, creditCard2, creditCard3, creditCard4));
	}
	@Test
	public void returnsCreditCardWhenNumberEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard4));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("number", SearchOperation.EQUALITY, "6011245599887744")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(creditCard4));
	}
	
	@Test
	public void returnsCreditCardWhenNumberBeginsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class)))
			.thenReturn(setupRepository(creditCard3));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(
				new SearchCriteria("number", SearchOperation.STARTS_WITH, "3548")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(creditCard3));
	}
	
	@Test
	public void returnsCreditCardWhenNumberEndsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard2));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(
				new SearchCriteria("number", SearchOperation.ENDS_WITH, "8210")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(creditCard2));
	}
	
	@Test
	public void returnsCreditCardWhenNumberContains() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard, creditCard4));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(
				new SearchCriteria("number", SearchOperation.CONTAINS, "24")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(creditCard, creditCard4));
	}

	@Test
	public void returnsCreditCardWhenNumberDoesntEqual() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard, creditCard2, creditCard3, creditCard4));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("number", SearchOperation.NEGATION, "1111111111111111")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(creditCard, creditCard2, creditCard3, creditCard4));
	}
	
	@Test
	public void returnsCreditCardWhenExpireMonthEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard, creditCard2));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("expireDate", SearchOperation.EQUALITY, "12")));
		assertThat(results.size(), is(2));
		assertThat(results, contains(creditCard, creditCard2));
	}
	
	@Test
	public void returnsCreditCardWhenExpireMonthBeginsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard, creditCard2));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("expireDate", SearchOperation.EQUALITY, "12")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(creditCard, creditCard2));
	}
	
	@Test
	public void returnsCreditCardWhenExpireMonthEndsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard, creditCard2));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("expireDate", SearchOperation.ENDS_WITH, "25")));
		assertThat(results.size(), is(2));
		assertThat(results, contains(creditCard, creditCard2));
	}
	
	@Test
	public void returnsCreditCardWhenExpireMonthContains() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard3, creditCard4));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("expireDate", SearchOperation.CONTAINS, "24")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(creditCard3, creditCard4));
	}

	@Test
	public void returnsCreditCardWhenExpireMonthDoesntEqual() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard, creditCard2, creditCard3, creditCard4));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("expireDate", SearchOperation.NEGATION, "12/55")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(creditCard, creditCard2, creditCard3, creditCard4));
	}
	@Test
	public void returnsCreditCardWhenExpireYrEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("expireDate", SearchOperation.EQUALITY, "12/25")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(creditCard));
	}
	
	@Test
	public void returnsCreditCardWhenExpireYrBeginsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard, creditCard2));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("expireDate", SearchOperation.EQUALITY, "12")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(creditCard, creditCard2));
	}
	
	@Test
	public void returnsCreditCardWhenExpireYrEndsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard, creditCard2));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("expireDate", SearchOperation.ENDS_WITH, "25")));
		assertThat(results.size(), is(2));
		assertThat(results, contains(creditCard, creditCard2));
	}
	
	@Test
	public void returnsCreditCardWhenExpireYrContains() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard3, creditCard4));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("expireDate", SearchOperation.CONTAINS, "24")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(creditCard3, creditCard4));
	}
	
	@Test
	public void returnsCreditCardWhenExpireYrDoesntEqual() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard, creditCard2, creditCard3, creditCard4));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("expireDate", SearchOperation.NEGATION, "12/55")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(creditCard, creditCard2, creditCard3, creditCard4));
	}

	@Test
	public void returnsCreditCardWhenCvvEquals() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("cvv", SearchOperation.EQUALITY, "123")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(creditCard));
	}
	
	@Test
	public void returnsCreditCardWhenCvvBeginsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard2));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("cvv", SearchOperation.STARTS_WITH, "45")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(creditCard2));
	}

	@Test
	public void returnsCreditCardWhenCvvEndsWith() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard3));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("cvv", SearchOperation.ENDS_WITH, "76")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(creditCard3));
	}

	@Test
	public void returnsCreditCardWhenCvvContains() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard2, creditCard4));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("cvv", SearchOperation.CONTAINS, "55")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(creditCard2, creditCard4));
	}

	@Test
	public void returnsCreditCardWhenCvvDoesntEqual() throws Exception {
		when(creditCardRepo.findAll(any(Specification.class))).thenReturn(setupRepository(creditCard, creditCard2, creditCard3, creditCard4));
		results = creditCardRepo.findAll(new BtgSpecification<CreditCard>(new SearchCriteria("cvv", SearchOperation.NEGATION, "069")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(creditCard, creditCard2, creditCard3, creditCard4));
	}
	
	private List<CreditCard> setupRepository(CreditCard...creditCards) {
		List<CreditCard> creditCardList = new ArrayList<CreditCard>();
		for (CreditCard aCreditCard : creditCards) {
			creditCardList.add(aCreditCard);
		}
		return creditCardList;
	}
}