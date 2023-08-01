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

import com.btg.website.model.Address;
import com.btg.website.repository.AddressRepository;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.util.AddressModelAssembler;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AddressRestController.class)
@SuppressWarnings("unchecked")
public class AddressRestControllerTest {

	@MockBean private AddressRepository addressRepo;
	@MockBean private AddressModelAssembler modelAssembler;
	@Autowired private MockMvc mockedRequest;
	
	private TestUtils<Address> addressUtils;
	private Address address, address2, address3, address4, address5, address6, address7, address8, address9, address10;
	private List<Address> addressList;
	
	@BeforeEach
	public void setup() {
		addressUtils = new TestUtils<Address>();
		address = new Address("1060 W Addison", "Chicago", null, "60613");
		address2 = new Address("1901 W Madison", "Chicago", null, "60612");
		address3 = new Address("333 W 35th St", "Chicago", null, "60616");
		address4 = new Address("233 S Wacker Dr", "Chicago", null, "60606");
		address5 = new Address("20 W 34th St", "New York", null, "10001");
		address6 = new Address("234 W 42nd St", "New York", null, "10036");
		address7 = new Address("1600 Pennsylvania Avenue NW", "Washington", null, "20500");
		address8 = new Address("First St SE", "Washington", null, "20004");
		address9 = new Address("4 Jersey St", "Boston", null, "02215");
		address10 = new Address("3700 Hogge Dr", "Parker", null, "75002");
		addressList = addressUtils.setupRepository(address, address2, address3, address4, address5, address6, address7, address8,
				address9, address10);
	}
	
	@Test
	public void createAddressWhenRequestIsValid() throws Exception {
		Address addressToSave = new Address("218 Sussex Ct", "North Aurora", null, "60542");
		when(addressRepo.save(any(Address.class))).thenReturn(addressToSave);
		MvcResult mvcResult = mockedRequest
				.perform(post("/btg/rest/review")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"street\" : \"218 Sussex Ct\", \"city\" : \"North Aurora\", \"zipCode\" : \"60542\"}")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returns404WhenIdIsNotFound() throws Exception {
		when(addressRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/address/0")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsAllAddressesWhenNoCriteriaIsProvided() throws Exception {
		when(addressRepo.findAll()).thenReturn(addressList);
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/admin/rest/addresses")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsAddressWhenIdIsFound() throws Exception {
		when(addressRepo.findById(anyLong())).thenReturn(Optional.of(address));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/address/1")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsAddressWhenStreetEquals() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "street:1060 W Addison")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsAddressWhenStreetBeginsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "street:1901*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	
	@Test
	public void returnsAddressWhenStreetEndsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address4, address10));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "street:*Dr")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	
	@Test
	public void returnsAddressWhenStreetContains() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address, address2, address3, address4, address5, address6, address7));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "street:*W*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());		
	}
	
	@Test
	public void returnsAddressWhenStreetDoesNotEqual() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address, address2, address3, address5, address6, address7, address8, address9, address10));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "street!233 S Wacker Dr")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());				
	}
	
	@Test
	public void returnsAddressWhenCityEquals() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address, address2, address3, address4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "city:Chicago")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());				
	}
	
	@Test
	public void returnsAddressWhenCityBeginsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address9));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "city:Bos*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());				
	}
	
	@Test
	public void returnsAddressWhenCityEndsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address5, address6));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "city:*York")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());				
	}
	
	@Test
	public void returnsAddressWhenCityContains() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address7, address8));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "city:*Wash*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());				
	}
	
	@Test
	public void returnsAddressWhenCityDoesNotEqual() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address5, address6, address7, address8, address9, address10));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "city!Chicago")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());				
	}
	
	@Test
	public void returnsAddressWhenZipCodeEquals() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "zipCode:60616")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());				
	}
	
	@Test
	public void returnsAddressWhenZipCodeBeginsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address5, address6));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "zipCode:10*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());				
	}
	
	@Test
	public void returnsAddressWhenZipCodeEndsWith() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address2, address10));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "zipCode:*2")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());				
	}
	
	@Test
	public void returnsAddressWhenZipCodeContains() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressUtils.setupRepository(address5, address6, address7, address8, address10));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "zipCode:*00*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());				
	}
	
	@Test
	public void returnsAddressWhenZipCodeDoesNotEqual() throws Exception {
		when(addressRepo.findAll(any(Specification.class))).thenReturn(addressList);
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/searchAddresses")
						.param("search", "zipCode:60542")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());				
	}
	
	@Test
	public void updatesAddressByIdWhenRequestIsValid() throws Exception {
		when(addressRepo.findById(anyLong())).thenReturn(Optional.of(address));
		when(addressRepo.save(any(Address.class))).thenReturn(address);
		MvcResult mvcResult = mockedRequest
				.perform(put("/btg/rest/address/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"street\" : \"218 Sussex Ct\", \"city\" : \"North Aurora\", \"state\" : { \"name\" : \"Illinois\", \"abv\" : \"IL\"}, \"zipCode\" : \"60542\" }")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.street").value("218 Sussex Ct"))
				.andExpect(jsonPath("$.city").value("North Aurora"))
				.andExpect(jsonPath("$.state.name").value("Illinois"))
				.andExpect(jsonPath("$.state.abv").value("IL"))
				.andExpect(jsonPath("$.zipCode").value("60542")).andReturn();
		Address foundAddress = addressRepo.findById(1L).get();
		assertThat(foundAddress.getStreet(), is("218 Sussex Ct"));
		assertThat(foundAddress.getCity(), is("North Aurora"));
		assertThat(foundAddress.getState().getName(), is("Illinois"));
		assertThat(foundAddress.getState().getAbv(), is("IL"));
		assertThat(foundAddress.getZipCode(), is("60542"));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesAddressStreetWhenRequestIsValid() throws Exception {
		when(addressRepo.findById(anyLong())).thenReturn(Optional.of(address));
		when(addressRepo.save(any(Address.class))).thenReturn(address);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/address/1")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/street\", \"value\" : \"218 Sussex Ct\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		address.setStreet("218 Sussex Ct");
		Address foundAddress = addressRepo.findById(1L).get();
		assertThat(foundAddress.getStreet(), is("218 Sussex Ct"));
		assertThat(foundAddress.getCity(), is(address.getCity()));
		assertThat(foundAddress.getState(), is(address.getState()));
		assertThat(foundAddress.getZipCode(), is(address.getZipCode()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesAddressCityWhenRequestIsValid() throws Exception {
		when(addressRepo.findById(anyLong())).thenReturn(Optional.of(address));
		when(addressRepo.save(any(Address.class))).thenReturn(address);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/address/1")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/city\", \"value\" : \"North Aurora\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		address.setCity("North Aurora");
		Address foundAddress = addressRepo.findById(1L).get();
		assertThat(foundAddress.getStreet(), is(address.getStreet()));
		assertThat(foundAddress.getCity(), is("North Aurora"));
		assertThat(foundAddress.getState(), is(address.getState()));
		assertThat(foundAddress.getZipCode(), is(address.getZipCode()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesAddressZipCodeWhenRequestIsValid() throws Exception {
		when(addressRepo.findById(anyLong())).thenReturn(Optional.of(address));
		when(addressRepo.save(any(Address.class))).thenReturn(address);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/rest/address/1")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/zipCode\", \"value\" : \"60542\"}]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		address.setZipCode("60542");
		Address foundAddress = addressRepo.findById(1L).get();
		assertThat(foundAddress.getStreet(), is(address.getStreet()));
		assertThat(foundAddress.getCity(), is(address.getCity()));
		assertThat(foundAddress.getState(), is(address.getState()));
		assertThat(foundAddress.getZipCode(), is("60542"));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void deletesAddressByIdWhenRequestIsValid() throws Exception {
		when(addressRepo.findById(anyLong())).thenReturn(Optional.of(address2));
		Address foundAddress = addressRepo.findById(2L).get();
		doAnswer(invocation -> {
			addressList.remove(1);
			return null;
		}).when(addressRepo).deleteById(anyLong());
		mockedRequest.perform(delete("/btg/rest/address/2")).andExpect(status().isNoContent());
		verify(addressRepo).deleteById(2L);
		assertThat(addressList.size(), is(9));
		assertThat(addressList, not(hasItem(foundAddress)));
	}
}