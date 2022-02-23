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
import com.btg.website.model.Company;
import com.btg.website.model.State;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

public class CompanyRepositoryTest {

	@MockBean
	AddressRepository addressRepo;

	@MockBean
	CompanyRepository companyRepo;

	@MockBean
	StateRepository stateRepo;

	private Company company, company2, company3;
	private Address address;
	private State state;
	private List<Company> repository;
	private List<Company> results;

	@BeforeEach
	public void setup() {
		companyRepo = mock(CompanyRepository.class);
		addressRepo = mock(AddressRepository.class);
		stateRepo = mock(StateRepository.class);
		state = new State("New York", "NY");
		address = new Address("20 W 34th St", "New York", state, "10001");

		when(stateRepo.findById(1L)).thenReturn(Optional.of(state));
		state = stateRepo.findById(1L).get();

		when(addressRepo.findById(1L)).thenReturn(Optional.of(address));
		address = addressRepo.findById(1L).get();
		company = new Company("ABCC Corp", address, null, .05, "222-234-3453");
		company2 = new Company("ABDEF Corp", address, address, .25, "222-222-3344");
		company3 = new Company("Acme Corps", address, null, .30, "773-777-0128");
		repository = setupRepository(company, company2, company3);
	}

	@Test
	public void returnsNoResultsWhenIdIsNotFound() throws Exception {
		when(companyRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<Company> customer = companyRepo.findById(-1L);
		assertThat(!customer.isPresent(), is(!customer.isPresent()));
	}

	@Test
	public void returnsCompanyWhenIdIsFound() throws Exception {
		when(companyRepo.findById(anyLong())).thenReturn(Optional.of(company));
		Optional<Company> foundCompany = companyRepo.findById(1L);
		assertThat(true, is(foundCompany.isPresent()));
		assertThat(foundCompany.get().getName(), is("ABCC Corp"));
		assertThat(foundCompany.get().getDiscount(), is(.05D));
		assertThat(foundCompany.get().getBillingAddress().getStreet(), is("20 W 34th St"));
		assertThat(foundCompany.get().getBillingAddress().getCity(), is("New York"));
		assertThat(foundCompany.get().getBillingAddress().getState(), is(state));
		assertThat(foundCompany.get().getBillingAddress().getZipCode(), is("10001"));
	}

	@Test
	public void returnsAllCompaniesWhenNoSearchCriteriaIsProvided() throws Exception {
		when(companyRepo.findAll()).thenReturn(repository);
		List<Company> returnedCompanies = new ArrayList<Company>();
		returnedCompanies = (List<Company>) companyRepo.findAll();
		assertThat(returnedCompanies.size(), is(3));
		assertThat(returnedCompanies, containsInAnyOrder(company, company2, company3));
	}

	@Test
	public void savesCompanyToRepositorySuccessfully() throws Exception {
		Company company = new Company("GHI", address, null, .45, "847-676-2644");
		when(companyRepo.save(any(Company.class))).thenReturn(company);
		Company newCompany = companyRepo.save(company);
		assertThat(newCompany.getBillingAddress().getStreet(), is("20 W 34th St"));
		assertThat(newCompany.getBillingAddress().getCity(), is("New York"));
		assertThat(newCompany.getBillingAddress().getState(), is(state));
		assertThat(newCompany.getBillingAddress().getZipCode(), is("10001"));
	}

	@Test
	public void savesMutipleCompaniesToRepositorySuccessfully() throws Exception {
		List<Company> listOfCompaniesToSave = new ArrayList<Company>();
		Company companyToSave = new Company("GHI", address, null, .45, "847-676-2644");
		Company companyToSave2 = new Company("JKL", address, null, .50, "847-477-0911");
		listOfCompaniesToSave.add(companyToSave);
		listOfCompaniesToSave.add(companyToSave2);
		when(companyRepo.saveAll(anyCollection())).thenReturn(listOfCompaniesToSave);
		List<Company> savedCompanies = companyRepo.saveAll(listOfCompaniesToSave);
		assertThat(savedCompanies.size(), is(2));
		assertThat(savedCompanies, containsInAnyOrder(companyToSave2, companyToSave));
	}

	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(companyRepo.count()).thenReturn((long) repository.size());
		long count = companyRepo.count();
		assertThat(count, is(3L));
	}

	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(companyRepo).deleteAll();
		companyRepo.deleteAll();
		verify(companyRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteCompanyFromRepositoryById() throws Exception {
		when(companyRepo.findById(anyLong())).thenReturn(Optional.of(company));
		Company foundCompany = companyRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(companyRepo).deleteById(anyLong());
		companyRepo.deleteById(1L);
		verify(companyRepo).deleteById(1L);
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(foundCompany)));
	}

	@Test
	public void deleteProvidedCollectionOfCompaniesFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(companyRepo).deleteAll(anyCollection());
		companyRepo.deleteAll(repository);
		verify(companyRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteCompaniesFromRepositoryByGivenIds() throws Exception {
		when(companyRepo.findAllById(anyCollection())).thenReturn(setupRepository(company, company2));
		doAnswer(invocation -> {
			Iterable<Company> addressesToDelete = companyRepo.findAllById(Arrays.asList(1L, 2L));
			addressesToDelete.forEach(anCompany -> repository.remove(anCompany));
			return null;
		}).when(companyRepo).deleteAllById(anyCollection());
		companyRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(companyRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(1));
		assertThat(repository, not(hasItem(company)));
		assertThat(repository, not(hasItem(company2)));
	}

	@Test
	public void deleteProvidedCompanySuccessfully() throws Exception {
		when(companyRepo.findById(anyLong())).thenReturn(Optional.of(company2));
		Company foundMovie = companyRepo.findById(5L).get();
		doAnswer(invocation -> {
			repository.remove(1);
			return null;
		}).when(companyRepo).delete(any(Company.class));
		companyRepo.delete(foundMovie);
		verify(companyRepo).delete(foundMovie);
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(company2)));
	}

	@Test
	public void returnsCompanyWhenCompanyNameEquals() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company3));
		results = companyRepo.findAll(
				new BtgSpecification<Company>(new SearchCriteria("name", SearchOperation.EQUALITY, "Acme Corps")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(company3));
	}

	@Test
	public void returnsCompanyWhenCompanyNameBeginsWith() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company, company2));
		results = companyRepo
				.findAll(new BtgSpecification<Company>(new SearchCriteria("name", SearchOperation.STARTS_WITH, "ABC")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(company, company2));
	}

	@Test
	public void returnsCompanyWhenCompanyNameContains() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company, company2));
		results = companyRepo
				.findAll(new BtgSpecification<Company>(new SearchCriteria("name", SearchOperation.CONTAINS, "AB")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(company, company2));
	}

	@Test
	public void returnsCompanyWhenCompanyNameEndsWith() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company3));
		results = companyRepo
				.findAll(new BtgSpecification<Company>(new SearchCriteria("name", SearchOperation.ENDS_WITH, "Corps")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(company3));
	}

	@Test
	public void returnsCompaniesWhenCompanyNameDoesntEqual() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company, company2, company3));
		results = companyRepo
				.findAll(new BtgSpecification<Company>(new SearchCriteria("name", SearchOperation.NEGATION, "Amazon")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(company, company2, company3));
	}

	@Test
	public void returnsCompanyWhenDiscountIsGreaterThanOrEqualTo() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company, company2));
		results = companyRepo.findAll(
				new BtgSpecification<Company>(new SearchCriteria("discount", SearchOperation.STARTS_WITH, .25)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(company, company2));
	}

	@Test
	public void returnsCompanyWhenDiscountIsLessThanOrEqualTo() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company));
		results = companyRepo
				.findAll(new BtgSpecification<Company>(new SearchCriteria("discount", SearchOperation.LESS_THAN, .10)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(company));
	}

	@Test
	public void returnsCompaniesWhenDiscountDoesntEqual() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company2, company3));
		results = companyRepo
				.findAll(new BtgSpecification<Company>(new SearchCriteria("discount", SearchOperation.NEGATION, .05)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(company2, company3));
	}
	
	public void returnsCompanyWhenCompanyPhoneNumberEquals() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company3));
		results = companyRepo.findAll(
				new BtgSpecification<Company>(new SearchCriteria("phoneNumber", SearchOperation.EQUALITY, "773-777-0128")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(company3));
	}

	@Test
	public void returnsCompanyWhenCompanyPhoneNumberBeginsWith() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company, company2));
		results = companyRepo
				.findAll(new BtgSpecification<Company>(new SearchCriteria("phoneNumber", SearchOperation.STARTS_WITH, "222-")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(company, company2));
	}

	@Test
	public void returnsCompanyWhenCompanyPhoneNumberContains() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company, company2));
		results = companyRepo
				.findAll(new BtgSpecification<Company>(new SearchCriteria("phoneNumber", SearchOperation.CONTAINS, "34")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(company, company2));
	}

	@Test
	public void returnsCompanyWhenCompanyPhoneNumberEndsWith() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company3));
		results = companyRepo
				.findAll(new BtgSpecification<Company>(new SearchCriteria("phoneNumber", SearchOperation.ENDS_WITH, "0128")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(company3));
	}

	@Test
	public void returnsCompaniesWhenCompanyPhoneNumberDoesntEqual() throws Exception {
		when(companyRepo.findAll(any(Specification.class))).thenReturn(setupRepository(company, company2, company3));
		results = companyRepo
				.findAll(new BtgSpecification<Company>(new SearchCriteria("phoneNumber", SearchOperation.NEGATION, "111-111-1111")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(company, company2, company3));
	}


	private List<Company> setupRepository(Company... companies) {
		List<Company> companyList = new ArrayList<Company>();
		for (Company aCustomer : companies) {
			companyList.add(aCustomer);
		}
		return companyList;
	}
}