package com.btg.website.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import com.btg.website.model.State;
import com.btg.website.repository.builder.BtgSpecificationBuilder;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;


public class StateRepositoryTest {

	private State state, state2, state3, state4, state5, state6, state7, state8, state9, state10;
	private List<State> repository;
	private BtgSpecificationBuilder<State> builder;
	
	@MockBean
	private StateRepository stateRepo;
	
	@BeforeEach
	public void setup() {
		stateRepo = mock(StateRepository.class);
		state = new State("Arizona", "AZ");
		state2 = new State("Florida", "FL");
		state3 = new State("Illinois", "IL");
		state4 = new State("Mississippi", "MS");
		state5 = new State("North Dakota", "ND");
		state6 = new State("South Dakota", "SD");
		state7 = new State("North Carolina", "NC");
		state8 = new State("South Carolina", "SC");
		repository = setupRepository(state, state2, state3, state4);
		builder = new BtgSpecificationBuilder<State>();
	}
	
	@Test
	public void returnsNoResultsWhenIdIsNotFound() throws Exception {
		when(stateRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<State> state = stateRepo.findById(-1L);
		assertThat(true, is(!state.isPresent()));
	}

	@Test
	public void returnsStateWhenIdIsFound() throws Exception {
		when(stateRepo.findById(anyLong())).thenReturn(Optional.of(state2));
		Optional<State> foundState = stateRepo.findById(2L);
		assertThat(true, is(foundState.isPresent()));
		assertThat(foundState.get().getName(), is("Florida"));
		assertThat(foundState.get().getAbv(), is("FL"));
	}
	
	@Test
	public void returnsAllStatesWhenNoSearchCriteriaIsProvided() throws Exception {
		when(stateRepo.findAll()).thenReturn(repository);
		List<State> returnedStates = stateRepo.findAll();
		assertThat(returnedStates.size(), is(4));
		assertThat(returnedStates, containsInAnyOrder(state,state2,state3,state4));
	}
	
	@Test
	public void returnsStateWhenNameIsFound() throws Exception {
		List<State> searchedState = new ArrayList<State>();
		when(stateRepo.findAll(any(Specification.class))).thenReturn(setupRepository(state3));
		SearchCriteria sc = new SearchCriteria("name", SearchOperation.EQUALITY, "Illinois");
		searchedState = stateRepo.findAll(new BtgSpecification<State>(sc));
		assertThat(searchedState.size(), is(1));
		assertThat(searchedState, contains(state3));
	}
	
	@Test
	public void givenNameOrAbv_whenGettingListOfStatees_thenCorrect() throws Exception {
		when(stateRepo.findAll(any(Specification.class))).thenReturn(setupRepository(state,state2));
		List<State> results = stateRepo
				.findAll(builder.with("name", ":", "Arizona", "", "").with("'", "abv", ":", "FL", "", "")
						.build(searchCriteria -> new BtgSpecification<State>((SearchCriteria) searchCriteria)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(state, state2));
	}
	
	@Test
	public void returnsStateWhenAbvIsFound() throws Exception {
		when(stateRepo.findAll(any(Specification.class))).thenReturn(setupRepository(state4));
		List<State> results = stateRepo
				.findAll(new BtgSpecification<State>(new SearchCriteria("abv", SearchOperation.EQUALITY, "MS")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(state4));
	}
	
	@Test
	public void returnsStateWhenNameBeginsWith() throws Exception {
		when(stateRepo.findAll(any(Specification.class))).thenReturn(setupRepository(state5, state7));
		List<State> results = stateRepo
				.findAll(new BtgSpecification<State>(new SearchCriteria("name", SearchOperation.STARTS_WITH, "North")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(state5, state7));
	}
	
	@Test
	public void returnsStateWhenNameEndsWith() throws Exception {
		when(stateRepo.findAll(any(Specification.class))).thenReturn(setupRepository(state5, state6));
		List<State> results = stateRepo
				.findAll(new BtgSpecification<State>(new SearchCriteria("name", SearchOperation.ENDS_WITH, "Dakota")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(state5, state6));
	}
	
	@Test
	public void returnsStateWhenNameContains() throws Exception {
		when(stateRepo.findAll(any(Specification.class))).thenReturn(setupRepository(state7,state8));
		List<State> results = stateRepo
				.findAll(new BtgSpecification<State>(new SearchCriteria("name", SearchOperation.CONTAINS, "Carolinal")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(state7, state8));
	}
	
	@Test
	public void returnsStatesWhenNameDoesntEqual() throws Exception {
		when(stateRepo.findAll(any(Specification.class))).thenReturn(setupRepository(state,state2,state3,state5,state6,state7,state8));
		List<State> results = stateRepo
				.findAll(new BtgSpecification<State>(new SearchCriteria("name", SearchOperation.NEGATION, "Mississippi")));
		assertThat(results.size(), is(7));
		assertThat(results, containsInAnyOrder(state, state2, state3, state5, 
				state6, state7, state8));
	}
	
	private List<State> setupRepository(State...states) {
		List<State> statesList = new ArrayList<State>();
		for(State aState : states) {
			statesList.add(aState);
		}
		return statesList;
	}
}