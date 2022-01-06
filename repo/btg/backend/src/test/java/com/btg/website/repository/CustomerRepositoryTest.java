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

import com.btg.website.model.Address;
import com.btg.website.model.Company;
import com.btg.website.model.Customer;
import com.btg.website.model.State;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

public class CustomerRepositoryTest {

	@MockBean
	AddressRepository addressRepo;
	
	@MockBean
	CustomerRepository customerRepo;
	
	@MockBean
	CompanyRepository companyRepo;
	
	@MockBean
	StateRepository stateRepo;
	
	private Customer customer, customer2, customer3, customer4;
	private Address address, address2;
	private Company company, company2;
	private State state;
	
	List<Customer> repository;
	List<Customer> results;
	
	@BeforeEach
	public void setup() {
		customerRepo = mock(CustomerRepository.class);
		addressRepo = mock(AddressRepository.class);
		companyRepo = mock(CompanyRepository.class);
		stateRepo = mock(StateRepository.class);
		
		when(stateRepo.findById(1L)).thenReturn(Optional.of(new State("Illinois", "IL")));
		when(addressRepo.findById(1L)).thenReturn(Optional.of(new Address(null, null, state, null)));
		when(addressRepo.findById(2L)).thenReturn(Optional.of(new Address(null, null, state, null)));
		when(companyRepo.findById(1L)).thenReturn(Optional.of(new Company(null, null, null, address, address2, 0, null)));
		when(companyRepo.findById(2L)).thenReturn(Optional.of(new Company(null, null, null, address, address2, 0, null)));
		
		state = stateRepo.findById(1L).get();
		address = addressRepo.findById(1L).get();
		address2 = addressRepo.findById(2L).get();
		company = companyRepo.findById(1L).get();
		company2 = companyRepo.findById(2L).get();
		
		customer = new Customer("Bob", "Smith", address, null, null, "bob.smith@comcast.com", "222-805-2222", "user1", "p@ssword", new Date(System.currentTimeMillis()));
		customer2 = new Customer("John", "smythe", address, null, null, "john.smythe@comcast.net", "312-781-1916", "user2", "Pword", new Date(System.currentTimeMillis()));
		customer3 = new Customer("Jon", "Doe", address, address2, company, "jon.doe@company.com", "312-693-0103", "jdoe", "P@ssw0rd", new Date(System.currentTimeMillis()));
		customer4 = new Customer("Tom", "Garcia", address, address2, company2, "tgarcia@company2.net", "773-805-3203", "tgarcia", "!P@ssW0rd", new Date(System.currentTimeMillis()));
		
		repository = setupRepository(customer, customer2, customer3, customer4);
	}
	
	@Test
	public void returnsNoResultsWhenIdIsNotFound() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<Customer> emptyCustomer = customerRepo.findById(-1L);
		assertThat(false, is(emptyCustomer.isPresent()));
	}
	
	@Test
	public void returnsAllCustomersWhenNoSearchCriteriaIsProvided() throws Exception {
		when(customerRepo.findAll()).thenReturn(repository);
		List<Customer> returnedCustomeres = (List<Customer>) customerRepo.findAll();
		assertThat(returnedCustomeres.size(), is(4));
		assertThat(returnedCustomeres, containsInAnyOrder(customer, customer2, customer3, customer4));
	}
	
	@Test
	public void returnsCustomerWhenIdIsFound() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer));
		Optional<Customer> foundCustomer = customerRepo.findById(1L);
		assertThat(true, is(foundCustomer.isPresent()));
		assertThat(foundCustomer.get().getFirstName(), is("Bob"));
		assertThat(foundCustomer.get().getLastName(), is("Smith"));
	}
	
	@Test
	public void savesCustomerToRepositorySuccessfully() throws Exception {
		when(addressRepo.findById(2L)).thenReturn(Optional.of(address2));
		when(companyRepo.findById(1L)).thenReturn(Optional.of(company));
		Address newCustomerAddress = addressRepo.findById(2L).get();
		Company newCustomerCompany = companyRepo.findById(1L).get();
		Customer customerToSave = new Customer("Bill","Clinton", newCustomerAddress, null, newCustomerCompany, "bill.clinton@whitehouse.gov", "312-555-0323", "user", "password", new Date(System.currentTimeMillis()));
		when(customerRepo.save(any(Customer.class))).thenReturn(customerToSave);
		Customer newCustomer = customerRepo.save(customerToSave);
		assertThat(newCustomer.getFirstName(), is("Bill"));
		assertThat(newCustomer.getLastName(), is("Clinton"));
	}
	
	@Test
	public void savesMutipleCustomerToRepositorySuccessfully() throws Exception {
		List<Customer> listOfCustomersToSave = new ArrayList<Customer>();
		Address customerAddressToSave = new Address("5244 W Brummel", "Skokie", state, "60077");
		Address customerAddressToSave2 = new Address("5701 W Oakton", "Skokie", state, "60077");
		Customer customerToSave = new Customer("New", "Customer", customerAddressToSave, null, null, "customer@email.com", "212-456-7854", "user22", "password", new Date(System.currentTimeMillis()));
		Customer customerToSave2 = new Customer("New2", "Customer2", customerAddressToSave2, null, null, "customer2@email.com", "847-452-3715", "user69", "password69", new Date(System.currentTimeMillis()));
		
		listOfCustomersToSave.add(customerToSave);
		listOfCustomersToSave.add(customerToSave2);
		when(customerRepo.saveAll(anyCollection())).thenReturn(listOfCustomersToSave);
		List<Customer> savedCustomers = customerRepo.saveAll(listOfCustomersToSave);
		assertThat(savedCustomers.size(), is(2));
		assertThat(savedCustomers, containsInAnyOrder(customerToSave2, customerToSave));
	}
	
	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(customerRepo.count()).thenReturn((long) repository.size());
		long count = customerRepo.count();
		assertThat(count, is(4L));
	}

	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(customerRepo).deleteAll();
		customerRepo.deleteAll();
		verify(customerRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteCustomerFromRepositoryById() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer4));
		Customer foundCustomer = customerRepo.findById(4L).get();
		doAnswer(invocation -> {
			repository.remove(3);
			return null;
		}).when(customerRepo).deleteById(anyLong());
		customerRepo.deleteById(4L);
		verify(customerRepo).deleteById(4L);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(foundCustomer)));
	}
	
	@Test
	public void deleteProvidedCollectionOfCustomersFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(customerRepo).deleteAll(anyCollection());
		customerRepo.deleteAll(repository);
		verify(customerRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteCustomersFromRepositoryByGivenIds() throws Exception {
		when(customerRepo.findAllById(anyCollection())).thenReturn(setupRepository(customer, customer2));
		doAnswer(invocation -> {
			Iterable<Customer> customeresToDelete = customerRepo.findAllById(Arrays.asList(1L, 2L));
			customeresToDelete.forEach(anAddress -> repository.remove(anAddress));
			return null;
		}).when(customerRepo).deleteAllById(anyCollection());
		customerRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(customerRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(customer)));
		assertThat(repository, not(hasItem(customer2)));
	}
	
	@Test
	public void deleteProvidedCustomerSuccessfully() throws Exception {
		when(customerRepo.findById(anyLong())).thenReturn(Optional.of(customer));
		Customer foundCustomer = customerRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(customerRepo).delete(any(Customer.class));
		customerRepo.delete(foundCustomer);
		verify(customerRepo).delete(foundCustomer);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(customer)));
	}
	
	@Test
	public void returnsCustomerWhenFirstNameEquals() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer));
		results = customerRepo.findAll(new BtgSpecification<Customer> (new SearchCriteria("firstName", SearchOperation.EQUALITY, "Bob")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(customer));
	}
	
	@Test
	public void returnsCustomerWhenFirstNameBeginsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer2, customer3));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("firstName", SearchOperation.STARTS_WITH, "Jo")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(customer2, customer3));
	}
	
	@Test
	public void returnsCustomerWhenFirstNameEndsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("firstName", SearchOperation.ENDS_WITH, "ob")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(customer));
	}
	
	@Test
	public void returnsCustomerWhenFirstNameContains() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer4));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("firstName", SearchOperation.CONTAINS, "To")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(customer4));
	}
	
	@Test
	public void returnsCustomerWhenFirstNameDoesntEqual() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer2, customer3));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("firstName", SearchOperation.NEGATION, "Robert")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(customer2, customer3));
	}
	
	@Test
	public void returnsCustomerWhenLastNameEquals() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer4));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("lastName", SearchOperation.EQUALITY, "Garcia")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(customer4));
	}
	
	@Test
	public void returnsCustomerWhenLastNameBeginsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer3));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("lastName", SearchOperation.STARTS_WITH, "Do")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(customer3));
	}
	
	@Test
	public void returnsCustomerWhenLastNameEndsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer3));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("lastName", SearchOperation.ENDS_WITH, "oe")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(customer3));
	}
	
	@Test
	public void returnsCustomerWhenLastNameContains() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer, customer2));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("lastName", SearchOperation.CONTAINS, "th")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(customer, customer2));
	}
	
	@Test
	public void returnsCustomerWhenLastNameDoesntEqual() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer2, customer3, customer4));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("lastName", SearchOperation.NEGATION, "Smith")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(customer2, customer3, customer4));
	}
	
	@Test
	public void returnsCustomerWhenEmailEquals() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("email", SearchOperation.EQUALITY, "bob.smith@comcast.com")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(customer));
	}
	
	@Test
	public void returnsCustomerWhenEmailBeginsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer2, customer3));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("email", SearchOperation.STARTS_WITH, "jo")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(customer2, customer3));
	}
	
	@Test
	public void returnsCustomerWhenEmailEndsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer2, customer4));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("email", SearchOperation.ENDS_WITH, ".net")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(customer2, customer4));
	}
	
	@Test
	public void returnsCustomerWhenEmailContains() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer3, customer4));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("email", SearchOperation.CONTAINS, "@company")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(customer3, customer4));
	}
	
	@Test
	public void returnsCustomerWhenEmailDoesntEqual() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer2, customer3, customer4));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("email", SearchOperation.NEGATION, "bob.smith@comcast.com")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(customer2, customer3, customer4));
	}
	
	@Test
	public void returnsCustomerWhenPhoneNumberEquals() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("phoneNumber", SearchOperation.EQUALITY, "222-805-2222")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(customer));
	}
	
	@Test
	public void returnsCustomerWhenPhoneNumberBeginsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer2, customer3));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("phoneNumber", SearchOperation.STARTS_WITH, "312-")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(customer2, customer3));
	}
	
	@Test
	public void returnsCustomerWhenPhoneNumberEndsWith() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer3, customer4));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("phoneNumber", SearchOperation.ENDS_WITH, "03")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(customer3, customer4));
	}
	
	@Test
	public void returnsCustomerWhenPhoneNumberContains() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer, customer2, customer3, customer4));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("phoneNumber", SearchOperation.CONTAINS, "-")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(customer, customer2, customer3, customer4));
	}
	
	@Test
	public void returnsCustomerWhenPhoneNumberDoesntEqual() throws Exception {
		when(customerRepo.findAll(any(Specification.class))).thenReturn(setupRepository(customer2, customer3, customer4));
		results = customerRepo.findAll(new BtgSpecification<Customer>(new SearchCriteria("phoneNumber", SearchOperation.NEGATION, "222-805-2222")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(customer2, customer3, customer4));
	}
	
	private List<Customer> setupRepository(Customer... customers) {
		List<Customer> customerList = new ArrayList<Customer>();
		for (Customer aCustomer : customers) {
			customerList.add(aCustomer);
		}
		return customerList;
	}
}