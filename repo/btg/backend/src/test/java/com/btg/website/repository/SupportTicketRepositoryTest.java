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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import com.btg.website.model.SupportTicket;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

public class SupportTicketRepositoryTest {
	
	@MockBean
	SupportTicketRepository supportTicketRepo;
	
	private SupportTicket supportTicket, supportTicket2, supportTicket3, supportTicket4;
	
	private List<SupportTicket> repository;
	private List<SupportTicket> results;
	
	@BeforeEach
	public void setup() {
		supportTicketRepo = mock(SupportTicketRepository.class);
		//17,712,000,000
		supportTicket = new SupportTicket("Customer ticket 1", "Customer reports website is down", new Date(86400000L), "opened", 1L);
		supportTicket2 = new SupportTicket("My Ticket", "One more ticket", new Date(157680000000L), "opened", 2L);
		supportTicket3 = new SupportTicket("Your Ticket to finish", "A Ticket to finish", new Date(31536000000L), "opened", 3L);
		supportTicket4 = new SupportTicket("Support Ticket", "A Opened support ticket", new Date(206928000000L), "opened", 4L);
		repository = setupRepository(supportTicket, supportTicket2, supportTicket3, supportTicket4);
	}
	
	@Test
	public void returnsNoResultWhenIdIsNotFound() throws Exception {
		when(supportTicketRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<SupportTicket> emptySupportTicket = supportTicketRepo.findById(-1L);
		assertThat(false, is(emptySupportTicket.isPresent()));
	}
	
	@Test
	public void returnsAllSupportTicketsWhenNoSearchCriteriaIsProvided() throws Exception {
		when(supportTicketRepo.findAll()).thenReturn(repository);
		List<SupportTicket> foundSupportTickets = supportTicketRepo.findAll();
		assertThat(foundSupportTickets.size(), is(4));
		assertThat(foundSupportTickets, containsInAnyOrder(supportTicket, supportTicket2, supportTicket3, supportTicket4));
	}
	
	@Test
	public void returnsSupportTicketWhenIdIsFound() throws Exception {
		when(supportTicketRepo.findById(anyLong())).thenReturn(Optional.of(supportTicket));
		Optional<SupportTicket> foundSupportTicket = supportTicketRepo.findById(1L);
		assertThat(true, is(foundSupportTicket.isPresent()));
		assertThat(foundSupportTicket.get().getTitle(), is("Customer ticket 1"));
		assertThat(foundSupportTicket.get().getDescription(), is("Customer reports website is down"));
	}
	
	@Test
	public void savesSupportTicketToRepositorySuccessfully() throws Exception {
		long milliseconds = System.currentTimeMillis();
		SupportTicket supportTicketToSave = new SupportTicket("Support Ticket 55", "Chat feature is down", new Date(milliseconds), "opened", 5L);
		when(supportTicketRepo.save(any(SupportTicket.class))).thenReturn(supportTicketToSave);
		SupportTicket newSupportTicket = supportTicketRepo.save(supportTicketToSave);
		assertThat(newSupportTicket.getTitle(), is("Support Ticket 55"));
		assertThat(newSupportTicket.getDescription(), is("Chat feature is down"));
		assertThat(newSupportTicket.getCreationDate(), is(supportTicketToSave.getCreationDate()));
		assertThat(newSupportTicket.getStatus(), is("opened"));
		assertThat(newSupportTicket.getCustomerId(), is(5L));
	}

	@Test
	public void savesMultipleSupportTicketToRepositorySuccessfully() throws Exception {
		long milliseconds = System.currentTimeMillis();
		List<SupportTicket> listOfSupportTicketsToSave = new ArrayList<SupportTicket>();
		SupportTicket supportTicketToSave = new SupportTicket("Support Ticket 55", "Chat feature is down", new Date(milliseconds), "opened", 5L);
		SupportTicket supportTicketToSave2 = new SupportTicket("Support Ticket 66", "Chat feature is down", new Date(milliseconds), "opened", 6L);
		
		listOfSupportTicketsToSave.add(supportTicketToSave);
		listOfSupportTicketsToSave.add(supportTicketToSave2);
		
		when(supportTicketRepo.saveAll(anyCollection())).thenReturn(listOfSupportTicketsToSave);
		List<SupportTicket> newSupportTickets = supportTicketRepo.saveAll(listOfSupportTicketsToSave);
		assertThat(newSupportTickets.size(), is(2));
		assertThat(newSupportTickets, containsInAnyOrder(supportTicketToSave, supportTicketToSave2));
	}

	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(supportTicketRepo.count()).thenReturn((long) repository.size());
		long count = supportTicketRepo.count();
		assertThat(count, is(4L));
	}

	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(supportTicketRepo).deleteAll();
		supportTicketRepo.deleteAll();
		verify(supportTicketRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteSupportTicketFromRepositoryById() throws Exception {
		when(supportTicketRepo.findById(anyLong())).thenReturn(Optional.of(supportTicket4));
		SupportTicket foundSupportTicket = supportTicketRepo.findById(4L).get();
		doAnswer(invocation -> {
			repository.remove(3);
			return null;
		}).when(supportTicketRepo).deleteById(anyLong());
		supportTicketRepo.deleteById(4L);
		verify(supportTicketRepo).deleteById(4L);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(foundSupportTicket)));
	}
	
	@Test
	public void deleteProvidedCollectionOfSupportTicketsFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(supportTicketRepo).deleteAll(anyCollection());
		supportTicketRepo.deleteAll(repository);
		verify(supportTicketRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteSupportTicketsFromRepositoryByGivenIds() throws Exception {
		when(supportTicketRepo.findAllById(anyCollection())).thenReturn(setupRepository(supportTicket, supportTicket2));
		doAnswer(invocation -> {
			Iterable<SupportTicket> supportTicketesToDelete = supportTicketRepo.findAllById(Arrays.asList(1L, 2L));
			supportTicketesToDelete.forEach(aSupportTicket -> repository.remove(aSupportTicket));
			return null;
		}).when(supportTicketRepo).deleteAllById(anyCollection());
		supportTicketRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(supportTicketRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(supportTicket)));
		assertThat(repository, not(hasItem(supportTicket2)));
	}
	
	@Test
	public void deleteProvidedSupportTicketSuccessfully() throws Exception {
		when(supportTicketRepo.findById(anyLong())).thenReturn(Optional.of(supportTicket));
		SupportTicket foundSupportTicket = supportTicketRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(supportTicketRepo).delete(any(SupportTicket.class));
		supportTicketRepo.delete(foundSupportTicket);
		verify(supportTicketRepo).delete(foundSupportTicket);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(supportTicket)));
	}

	@Test
	public void returnsSupportTicketWhenTitleEquals() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket> (new SearchCriteria("title", SearchOperation.EQUALITY, "SupportTicket")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(supportTicket));
	}
	
	@Test
	public void returnsSupportTicketWhenTitleBeginsWith() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket2));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("title", SearchOperation.STARTS_WITH, "My")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(supportTicket2));
	}
	
	@Test
	public void returnsSupportTicketWhenTitleEndsWith() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket3));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("title", SearchOperation.ENDS_WITH, "to finish")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(supportTicket3));
	}
	
	@Test
	public void returnsSupportTicketWhenTitleContains() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket, supportTicket4));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("title", SearchOperation.CONTAINS, "Support")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(supportTicket, supportTicket4));
	}
	
	@Test
	public void returnsSupportTicketWhenTitleDoesntEqual() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket, supportTicket2, supportTicket3));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("title", SearchOperation.NEGATION, "Support Ticket")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(supportTicket, supportTicket2, supportTicket3));
	}
	
	@Test
	public void returnsSupportTicketWhenDescriptionEquals() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket> (new SearchCriteria("description", SearchOperation.EQUALITY, "Customer reports website is down")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(supportTicket));
	}
	
	@Test
	public void returnsSupportTicketWhenDescriptionBeginsWith() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket2));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("description", SearchOperation.STARTS_WITH, "One")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(supportTicket2));
	}
	
	@Test
	public void returnsSupportTicketWhenDescriptionEndsWith() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket3));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("description", SearchOperation.ENDS_WITH, "finish")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(supportTicket3));
	}
	
	@Test
	public void returnsSupportTicketWhenDescriptionContains() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket2, supportTicket3, supportTicket4));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("description", SearchOperation.CONTAINS, "ticket")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(supportTicket2, supportTicket3, supportTicket4));
	}
	
	@Test
	public void returnsSupportTicketWhenDescriptionDoesntEqual() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket, supportTicket2, supportTicket3));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("description", SearchOperation.NEGATION, "A Opened support ticket")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(supportTicket, supportTicket2, supportTicket3));
	}
	
	@Test
	public void returnsSupportTicketWhenCreatedDateEquals() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket4));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket> (new SearchCriteria("createdDate", SearchOperation.EQUALITY, 206928000000L)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(supportTicket4));
	}
	
	@Test
	public void returnsSupportTicketWhenCreatedDateBeginsWith() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket2));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("createdDate", SearchOperation.STARTS_WITH, 157)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(supportTicket2));
	}
	
	@Test
	public void returnsSupportTicketWhenCreatedDateEndsWith() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket, supportTicket2, supportTicket3, supportTicket4));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("createdDate", SearchOperation.ENDS_WITH, 000)));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(supportTicket, supportTicket2, supportTicket3, supportTicket4));
	}
	
	@Test
	public void returnsSupportTicketWhenCreatedDateContains() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket2, supportTicket3));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("createdDate", SearchOperation.CONTAINS, 5)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(supportTicket2, supportTicket3));
	}
	
	@Test
	public void returnsSupportTicketWhenCreatedDateDoesntEqual() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket, supportTicket2, supportTicket3, supportTicket4));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("createdDate", SearchOperation.NEGATION, new Date(System.currentTimeMillis()))));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(supportTicket, supportTicket2, supportTicket3, supportTicket4));
	}
	
	@Test
	public void returnsSupportTicketWhenCreatedDateIsBefore() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket, supportTicket2, supportTicket3, supportTicket4));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("createdDate", SearchOperation.LESS_THAN, new Date(System.currentTimeMillis()))));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(supportTicket, supportTicket2, supportTicket3, supportTicket4));
	}

	@Test
	public void returnsSupportTicketWhenCreatedDateIsAfter() throws Exception {
		when(supportTicketRepo.findAll(any(Specification.class))).thenReturn(setupRepository(supportTicket, supportTicket2, supportTicket3, supportTicket4));
		results = supportTicketRepo.findAll(new BtgSpecification<SupportTicket>(new SearchCriteria("createdDate", SearchOperation.GREATER_THAN, new Date(0))));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(supportTicket, supportTicket2, supportTicket3, supportTicket4));
	}
	
	private List<SupportTicket> setupRepository(SupportTicket...supportTickets) {
		List<SupportTicket> supportTicketList = new ArrayList<SupportTicket>();
		for(SupportTicket aSupportTicket : supportTickets) {
			supportTicketList.add(aSupportTicket);
		}
		return supportTicketList;
	}
}