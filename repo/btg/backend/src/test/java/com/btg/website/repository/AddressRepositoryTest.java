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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import com.btg.website.model.Address;
import com.btg.website.model.State;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

@SuppressWarnings("unchecked")
public class AddressRepositoryTest {

	@MockBean
	AddressRepository addressRepo;

	@MockBean
	StateRepository stateRepo;
	
	private Address address, address2, address3, address4, address5, address6, address7, address8, address9, address10;
	private State state, state2, state3, state4, state5;
	private BtgSpecificationBuilder<Address> builder;
	private List<Address> results;
	private static List<Address> repository;

	@BeforeEach
	public void setup() {
		
		builder = new BtgSpecificationBuilder<Address>();
		state = new State("Illinois", "IL");
		state2 = new State("New York", "NY");
		state3 = new State("District of Columbia", "DC");
		state4 = new State("Massachusetts", "MA");
		state5 = new State("Texas", "TX");
		
		addressRepo = mock(AddressRepository.class);
		stateRepo = mock(StateRepository.class);

		when(stateRepo.findById(1L)).thenReturn(Optional.of(state));
		state = stateRepo.findById(1L).get();
		address = new Address("1060 W Addison", "Chicago", state, "60613");
		address2 = new Address("1901 W Madison", "Chicago", state, "60612");
		address3 = new Address("333 W 35th St", "Chicago", state, "60616");
		address4 = new Address("233 S Wacker Dr", "Chicago", state, "60606");
		
		when(stateRepo.findById(2L)).thenReturn(Optional.of(state2));
		state2 = stateRepo.findById(2L).get();
		address5 = new Address("20 W 34th St", "New York", state2, "10001");
		address6 = new Address("234 W 42nd St", "New York", state2, "10036");
		
		when(stateRepo.findById(3L)).thenReturn(Optional.of(state3));
		state3 = stateRepo.findById(3L).get();
		address7 = new Address("1600 Pennsylvania Avenue NW", "Washington", state3, "20500");
		address8 = new Address("First St SE", "Washington", state3, "20004");

		when(stateRepo.findById(4L)).thenReturn(Optional.of(state4));
		state4 = stateRepo.findById(4L).get();
		address9 = new Address("4 Jersey St", "Boston", state4, "02215");
		
		when(stateRepo.findById(5L)).thenReturn(Optional.of(state5));
		state5 = stateRepo.findById(5L).get();
		address10 = new Address("3700 Hogge Dr", "Parker", state5, "75002");
		repository = setupRepository(address, address2, address3, address4, address5, address6, address7, address8,
				address9, address10);
	}

	@Test
	public void returnsNoResultsWhenIdIsNotFound() throws Exception {
		Optional<Address> add = Optional.empty();
		when(addressRepo.findById(anyLong())).thenReturn(add);
		add = addressRepo.findById(-1L);
		assertThat(!add.isPresent(), is(!add.isPresent()));
	}

	@Test
	public void returnsAddressWhenIdIsFound() throws Exception {
		Optional<Address> foundAddress = Optional.of(address4);
		when(addressRepo.findById(anyLong())).thenReturn(foundAddress);
		foundAddress = addressRepo.findById(4L);
		assertThat(true, is(foundAddress.isPresent()));
		assertThat(foundAddress.get().getStreet(), is("233 S Wacker Dr"));
		assertThat(foundAddress.get().getCity(), is("Chicago"));
		assertThat(foundAddress.get().getState(), is(state));
		assertThat(foundAddress.get().getZipCode(), is("60606"));
	}

	@Test
	public void returnsAllAddressesWhenNoSearchCriteriaIsProvided() throws Exception {
		when(addressRepo.findAll()).thenReturn(repository);
		List<Address> returnedAddresses = new ArrayList<Address>();
		returnedAddresses = (List<Address>) addressRepo.findAll();
		assertThat(returnedAddresses.size(), is(10));
		assertThat(returnedAddresses, containsInAnyOrder(address, address2, address3, address4, address5, address7,
				address6, address8, address9, address10));
	}

	@Test
	public void savesAddressToRepositorySuccessfully() throws Exception {
		Address address = new Address("5244 W Brummel", "Skokie", state, "60077");
		when(addressRepo.save(any(Address.class))).thenReturn(address);
		Address newAddress = addressRepo.save(address);
		assertThat(newAddress.getStreet(), is("5244 W Brummel"));
		assertThat(newAddress.getCity(), is("Skokie"));
		assertThat(newAddress.getState(), is(state));
		assertThat(newAddress.getZipCode(), is("60077"));
	}

	@Test
	public void savesMutipleAddressesToRepositorySuccessfully() throws Exception {
		List<Address> listOfAddressessToSave = new ArrayList<Address>();
		Address addressToSave = new Address("5244 W Brummel", "Skokie", state, "60077");
		Address addressToSave2 = new Address("5701 W Oakton", "Skokie", state, "60077");
		listOfAddressessToSave.add(addressToSave);
		listOfAddressessToSave.add(addressToSave2);
		when(addressRepo.saveAll(anyCollection())).thenReturn(listOfAddressessToSave);
		List<Address> savedAddresses = addressRepo.saveAll(listOfAddressessToSave);
		assertThat(savedAddresses.size(), is(2));
		assertThat(savedAddresses, containsInAnyOrder(addressToSave2, addressToSave));
	}

	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(addressRepo.count()).thenReturn((long) repository.size());
		long count = addressRepo.count();
		assertThat(count, is(10L));
	}

	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(addressRepo).deleteAll();
		addressRepo.deleteAll();
		verify(addressRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteAddressFromRepositoryById() throws Exception {
		when(addressRepo.findById(anyLong())).thenReturn(Optional.of(address5));
		Address foundAddress = addressRepo.findById(5L).get();
		doAnswer(invocation -> {
			repository.remove(4);
			return null;
		}).when(addressRepo).deleteById(anyLong());
		addressRepo.deleteById(5L);
		verify(addressRepo).deleteById(5L);
		assertThat(repository.size(), is(9));
		assertThat(repository, not(hasItem(foundAddress)));
	}

	@Test
	public void deleteProvidedCollectionOfAddressesFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(addressRepo).deleteAll(anyCollection());
		addressRepo.deleteAll(repository);
		verify(addressRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteAddressesFromRepositoryByGivenIds() throws Exception {
		when(addressRepo.findAllById(anyCollection())).thenReturn(setupRepository(address, address2));
		doAnswer(invocation -> {
			Iterable<Address> addressesToDelete = addressRepo.findAllById(Arrays.asList(1L, 2L));
			addressesToDelete.forEach(anAddress -> repository.remove(anAddress));
			return null;
		}).when(addressRepo).deleteAllById(anyCollection());
		addressRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(addressRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(8));
		assertThat(repository, not(hasItem(address)));
		assertThat(repository, not(hasItem(address2)));
	}

	@Test
	public void deleteProvidedMovieSuccessfully() throws Exception {
		when(addressRepo.findById(anyLong())).thenReturn(Optional.of(address5));
		Address foundMovie = addressRepo.findById(5L).get();
		doAnswer(invocation -> {
			repository.remove(4);
			return null;
		}).when(addressRepo).delete(any(Address.class));
		addressRepo.delete(foundMovie);
		verify(addressRepo).delete(foundMovie);
		assertThat(repository.size(), is(9));
		assertThat(repository, not(hasItem(address5)));
	}
	
	@Test
	public void returnsAddressWhenCityEquals() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address10));
		results = addressRepo.findAll(new BtgSpecification<Address>(new SearchCriteria("city", SearchOperation.EQUALITY, "Dallas")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(address10));
	}
	
	@Test
	public void returnsAddressWhenCityBeginsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address5, address6));
		results = addressRepo.findAll(new BtgSpecification<Address>(new SearchCriteria("city", SearchOperation.STARTS_WITH, "New")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(address5, address6));
	}

	@Test
	public void returnsAddressWhenCityEndsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address7, address8, address9));
		results = addressRepo.findAll(new BtgSpecification<Address>(new SearchCriteria("city", SearchOperation.ENDS_WITH, "ton")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(address7, address8, address9));
	}
	
	@Test
	public void returnsAddressWhenCityContains() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address, address2, address3, address4));
		results = addressRepo
				.findAll(new BtgSpecification<Address>(new SearchCriteria("city", SearchOperation.CONTAINS, "hi")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(address, address2, address3, address4));
	}
	
	@Test
	public void returnsAddressWhenCityNameOrStateisGiven() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address, address2, address3, address4, address10));
		results = addressRepo
				.findAll(builder.with("city", ":", "Chicago").with("'", "state", ":", state5, "", "")
						.build(searchCriteria -> new BtgSpecification<Address>((SearchCriteria) searchCriteria)));
		assertThat(results.size(), is(5));
		assertThat(results, containsInAnyOrder(address, address2, address3, address4, address10));
	}

	@Test
	public void returnsAddressesWhenCityDoesntEqual() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address5, address6, address7, address8, address9, address10));
		results = addressRepo
				.findAll(new BtgSpecification<Address>(new SearchCriteria("city", SearchOperation.NEGATION, "Chicago")));
		assertThat(results.size(), is(6));
		assertThat(results, containsInAnyOrder(address5, address6, address7, address8, address9, address10));
	}

	@Test
	public void returnsAddressWhenStreetEquals() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address10));
		results = addressRepo.findAll(new BtgSpecification<Address>(new SearchCriteria("street", SearchOperation.EQUALITY, "3700 Hogge Dr")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(address10));
	}
	
	@Test
	public void returnsAddressWhenStreetBeginsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address, address2, address7));
		results = addressRepo.findAll(new BtgSpecification<Address>(new SearchCriteria("street", SearchOperation.STARTS_WITH, "1")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(address, address2, address7));
	}

	@Test
	public void returnsAddressWhenStreetEndsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address3,address5, address6, address9));
		results = addressRepo.findAll(new BtgSpecification<Address>(new SearchCriteria("street", SearchOperation.ENDS_WITH, "st")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(address3,address5, address6, address9));
	}
	
	
	@Test
	public void returnsAddressWhenStreetContains() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address, address2, address3, address5, address6, address7));
		results = addressRepo
				.findAll(new BtgSpecification<Address>(new SearchCriteria("city", SearchOperation.CONTAINS, "W")));
		assertThat(results.size(), is(6));
		assertThat(results, containsInAnyOrder(address, address2, address3, address5, address6, address7));
	}
	
	@Test
	public void returnsAddressesWhenStreetDoesntEqual() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address2, address3, address4, address5, address6, address7, address8, address9, address10));
		results = addressRepo
				.findAll(new BtgSpecification<Address>(new SearchCriteria("street", SearchOperation.NEGATION, "Addison")));
		assertThat(results.size(), is(9));
		assertThat(results, containsInAnyOrder(address2, address3, address4, address5, address6, address7, address8, address9, address10));
	}

	@Test
	public void returnsAddressWhenZipCodeEquals() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address10));
		results = addressRepo.findAll(new BtgSpecification<Address>(new SearchCriteria("zipCode", SearchOperation.EQUALITY, "75002")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(address10));
	}
	
	@Test
	public void returnsAddressWhenZipCodeBeginsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address, address2, address3, address4));
		results = addressRepo.findAll(new BtgSpecification<Address>(new SearchCriteria("zipCode", SearchOperation.STARTS_WITH, "60")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(address, address2, address3, address4));
	}

	@Test
	public void returnsAddressWhenZipCodeEndsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address3, address4, address6));
		results = addressRepo.findAll(new BtgSpecification<Address>(new SearchCriteria("zipCode", SearchOperation.ENDS_WITH, "6")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(address3, address4, address6));
	}
	
	@Test
	public void returnsAddressWhenZipCodeContains() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address, address2, address3, address4));
		results = addressRepo
				.findAll(new BtgSpecification<Address>(new SearchCriteria("zipCode", SearchOperation.CONTAINS, "06")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(address, address2, address3, address4));
	}
	
	@Test
	public void returnsAddressesWhenZipCodeDoesntEqual() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(setupRepository(address, address2, address3, address4, address5, address6, address7, address8, address9, address10));
		results = addressRepo
				.findAll(new BtgSpecification<Address>(new SearchCriteria("zipCode", SearchOperation.NEGATION, "60187")));
		assertThat(results.size(), is(10));
		assertThat(results, containsInAnyOrder(address, address2, address3, address4, address5, address6, address7, address8, address9, address10));
	}

	private List<Address> setupRepository(Address... addresses) {
		List<Address> addressList = new ArrayList<Address>();
		for (Address anAddress : addresses) {
			addressList.add(anAddress);
		}
		return addressList;
	}
}