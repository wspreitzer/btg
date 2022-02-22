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

import com.btg.website.model.Service;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

public class ServiceRepositoryTest {

	@MockBean
	ServiceRepository serviceRepo;

	private Service service, service2, service3, service4;
	
	private List<Service> repository;
	private List<Service> results;
	
	@BeforeEach
	public void setup() {
		serviceRepo = mock(ServiceRepository.class);
		service = new Service("Website Development", "We will build your website", 7299.99);
		service2 = new Service("Customer Service Management System", "We will create a full feature Service Service Managment System for you", 7499.99);
		service3 = new Service("Social Media Platform", "We will create your great social media presence ", 1499.95);
		service4 = new Service("Website Hosting", "We will provide and host a great domain for you", 499.95);
		
		repository = setupRepository(service, service2, service3, service4);
	}
	
	@Test
	public void returnsNoResultsWhenIdIsNotFound() throws Exception {
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<Service> emptyService = serviceRepo.findById(1L);
		assertThat(false, is(emptyService.isPresent()));
	}
	
	@Test
	public void returnsAllServicesWhenNoSearchCriteriaIsProvided() throws Exception {
		when(serviceRepo.findAll()).thenReturn(repository);
		List<Service> foundServices = serviceRepo.findAll();
		assertThat(foundServices.size(), is(4));
		assertThat(foundServices, containsInAnyOrder(service, service2, service3, service4));
	}
	
	@Test
	public void returnsServiceWhenIdIsFound() throws Exception {
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.of(service));
		Optional<Service> foundService = serviceRepo.findById(1L);
		assertThat(true, is(foundService.isPresent()));
		assertThat(foundService.get().getServiceName(), is("Website Development"));
		assertThat(foundService.get().getDescription(), is("We will build your website"));
		assertThat(foundService.get().getPrice(), is(7299.99));
	}
	
	@Test
	public void savesServiceToRepositorySuccessfully() throws Exception {
		Service serviceToSave = new Service("Create Database", "We will create a database", 199.99);
		when(serviceRepo.save(any(Service.class))).thenReturn(serviceToSave);
		Service newService = serviceRepo.save(serviceToSave);
		assertThat(newService.getServiceName(), is("Create Database"));
		assertThat(newService.getDescription(), is("We will create a database"));
		assertThat(newService.getPrice(), is(199.99));
		
	}
	
	@Test
	public void savesMutipleServiceToRepositorySuccessfully() throws Exception {
		List<Service> listOfServicesToSave = new ArrayList<Service>();
		Service serviceToSave = new Service("New", "Service", 199.99);
		Service serviceToSave2 = new Service("New2", "Service2", 299.99);
		
		listOfServicesToSave.add(serviceToSave);
		listOfServicesToSave.add(serviceToSave2);
		when(serviceRepo.saveAll(anyCollection())).thenReturn(listOfServicesToSave);
		List<Service> savedServices = serviceRepo.saveAll(listOfServicesToSave);
		assertThat(savedServices.size(), is(2));
		assertThat(savedServices, containsInAnyOrder(serviceToSave2, serviceToSave));
	}
	
	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(serviceRepo.count()).thenReturn((long) repository.size());
		long count = serviceRepo.count();
		assertThat(count, is(4L));
	}

	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(serviceRepo).deleteAll();
		serviceRepo.deleteAll();
		verify(serviceRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteServiceFromRepositoryById() throws Exception {
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.of(service4));
		Service foundService = serviceRepo.findById(4L).get();
		doAnswer(invocation -> {
			repository.remove(3);
			return null;
		}).when(serviceRepo).deleteById(anyLong());
		serviceRepo.deleteById(4L);
		verify(serviceRepo).deleteById(4L);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(foundService)));
	}
	
	@Test
	public void deleteProvidedCollectionOfServicesFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(serviceRepo).deleteAll(anyCollection());
		serviceRepo.deleteAll(repository);
		verify(serviceRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteServicesFromRepositoryByGivenIds() throws Exception {
		when(serviceRepo.findAllById(anyCollection())).thenReturn(setupRepository(service, service2));
		doAnswer(invocation -> {
			Iterable<Service> serviceesToDelete = serviceRepo.findAllById(Arrays.asList(1L, 2L));
			serviceesToDelete.forEach(anAddress -> repository.remove(anAddress));
			return null;
		}).when(serviceRepo).deleteAllById(anyCollection());
		serviceRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(serviceRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(service)));
		assertThat(repository, not(hasItem(service2)));
	}
	
	@Test
	public void deleteProvidedServiceSuccessfully() throws Exception {
		when(serviceRepo.findById(anyLong())).thenReturn(Optional.of(service));
		Service foundService = serviceRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(serviceRepo).delete(any(Service.class));
		serviceRepo.delete(foundService);
		verify(serviceRepo).delete(foundService);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(service)));
	}
	
	@Test
	public void returnsServiceWhenServiceNameEquals() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("serviceName", SearchOperation.EQUALITY, "Website Development")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(service));
	}
	
	@Test
	public void returnsServiceWhenServiceNameBeginsWith() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service, service4));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("serviceName", SearchOperation.STARTS_WITH, "Web")));
		assertThat(results.size(), is(2));
		assertThat(results, contains(service, service4));
	}
	
	@Test
	public void returnsServiceWhenServiceNameEndsWith() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service3));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("serviceName", SearchOperation.ENDS_WITH, "presence")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(service3));
	}
	
	@Test
	public void returnsServiceWhenServiceNameContains() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service, service4));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("serviceName", SearchOperation.CONTAINS, "contains")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(service, service4));
	}
	
	@Test
	public void returnsServiceWhenServiceNameDoesntEqual() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service, service2, service3, service4));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("serviceName", SearchOperation.NEGATION, "MicroService")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(service, service2, service3, service4));
	}
	
	@Test
	public void returnsServiceWhenDescriptionEquals() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("description", SearchOperation.EQUALITY, "We will build")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(service));
	}
	
	@Test
	public void returnsServiceWhenDescriptionBeginsWith() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service2, service3));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("description", SearchOperation.STARTS_WITH, "We will create")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(service2, service3));
	}
	
	@Test
	public void returnsServiceWhenDescriptionEndsWith() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service2, service4));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("description", SearchOperation.ENDS_WITH, "for you")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(service2, service4));
	}
	
	@Test
	public void returnsServiceWhenDescriptionContains() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service3, service4));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("description", SearchOperation.CONTAINS, "great")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(service3, service4));
	}
	
	@Test
	public void returnsServiceWhenDescriptionDoesntEqual() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service, service2, service3, service4));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("description", SearchOperation.NEGATION, "no description")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(service, service2, service3, service4));
	}
	
	@Test
	public void returnsServiceWhenPriceEquals() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("price", SearchOperation.EQUALITY, 7299.99)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(service));
	}
	
	@Test
	public void returnsServiceWhenPriceBeginsWith() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service, service2));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("price", SearchOperation.STARTS_WITH, 7)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(service, service2));
	}
	
	@Test
	public void returnsServiceWhenPriceEndsWith() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service3, service4));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("price", SearchOperation.ENDS_WITH, .95)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(service3, service4));
	}
	
	@Test
	public void returnsServiceWhenPriceContains() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service, service2));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("price", SearchOperation.CONTAINS, 2)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(service, service2));
	}
	
	@Test
	public void returnsServiceWhenPriceDoesntEqual() throws Exception {
		when(serviceRepo.findAll(any(Specification.class))).thenReturn(setupRepository(service, service2, service3, service4));
		results = serviceRepo.findAll(new BtgSpecification<Service>(new SearchCriteria("price", SearchOperation.NEGATION, .01)));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(service, service2, service3, service4));
	}
	
	private List<Service> setupRepository(Service...services) {
		List<Service> servicesList = new ArrayList<Service>();
		for(Service aService : services) {
			servicesList.add(aService);
		}
		return servicesList;
	}
}