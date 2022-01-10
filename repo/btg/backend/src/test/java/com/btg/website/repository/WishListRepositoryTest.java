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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import com.btg.website.model.WishList;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

public class WishListRepositoryTest {
	
	@MockBean
	private WishListRepository wishListRepo;
	
	private WishList wishList, wishList2, wishList3, wishList4;
	
	SimpleDateFormat fmt;
	
	private List<WishList> repository;
	private List<WishList> results;
	
	@BeforeEach
	public void setup() {
		wishListRepo = mock(WishListRepository.class);
		wishList = new WishList(1L, 1L, new Date(31536000000L));
		wishList2 = new WishList(2L, 2L, new Date(157680000000L));
		wishList3 = new WishList(3L, 3L, new Date(315360000000L));
		wishList4 = new WishList(4L, 4L, new Date(473040000000L));
		repository = setupRepository(wishList, wishList2, wishList3, wishList4);
		fmt = new SimpleDateFormat("MM/DD/YYYY");
	}
	
	@Test
	public void returnsNoResultsWhenIdsNotFound() throws Exception {
		when(wishListRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<WishList> emptyWishList = wishListRepo.findById(-1L);
		assertThat(false, is(emptyWishList.isPresent()));
	}
	
	@Test
	public void returnsAllWishListItemsWhenNoSearchCriteriaIsProvided() throws Exception {
		when(wishListRepo.findAll()).thenReturn(repository);
		List<WishList> returnedWishList = wishListRepo.findAll();
		assertThat(returnedWishList.size(), is(4));
		assertThat(returnedWishList, containsInAnyOrder(wishList, wishList2, wishList3, wishList4));
	}
	
	@Test
	public void returnsWishListWhenIdIsFound() throws Exception {
		when(wishListRepo.findById(anyLong())).thenReturn(Optional.of(wishList));
		Optional<WishList> foundWishList = wishListRepo.findById(1L);
		assertThat(true, is(foundWishList.isPresent()));
		assertThat(fmt.format(foundWishList.get().getAddedDate()), is(formatDateAsString(foundWishList.get().getAddedDate())));
	}

	@Test
	public void savesWishListToRepositorySuccessfully() throws Exception {
		WishList wishListToSave = new WishList(5L,5L, new Date(473040000000L));
		when(wishListRepo.save(any(WishList.class))).thenReturn(wishListToSave);
		WishList newWishList = wishListRepo.save(wishListToSave);
		assertThat(newWishList.getCustomerId(), is(5L));
		assertThat(newWishList.getProductId(), is(5L));
		assertThat(fmt.format(newWishList.getAddedDate()), is(formatDateAsString(newWishList.getAddedDate())));
	}
	
	@Test
	public void savesMutipleWishListToRepositorySuccessfully() throws Exception {
		List<WishList> listOfWishListsToSave = new ArrayList<WishList>();
		
		WishList wishListToSave = new WishList(5L,5L, new Date(473040000000L));
		WishList wishListToSave2 = new WishList(6L,6L, new Date(473040000000L));
		listOfWishListsToSave.add(wishListToSave);
		listOfWishListsToSave.add(wishListToSave2);
		when(wishListRepo.saveAll(anyCollection())).thenReturn(listOfWishListsToSave);
		List<WishList> savedWishLists = wishListRepo.saveAll(listOfWishListsToSave);
		assertThat(savedWishLists.size(), is(2));
		assertThat(savedWishLists, containsInAnyOrder(wishListToSave2, wishListToSave));
	}
	
	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(wishListRepo.count()).thenReturn((long) repository.size());
		long count = wishListRepo.count();
		assertThat(count, is(4L));
	}
	
	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(wishListRepo).deleteAll();
		wishListRepo.deleteAll();
		verify(wishListRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteWishListFromRepositoryById() throws Exception {
		when(wishListRepo.findById(anyLong())).thenReturn(Optional.of(wishList4));
		WishList foundWishList = wishListRepo.findById(4L).get();
		doAnswer(invocation -> {
			repository.remove(3);
			return null;
		}).when(wishListRepo).deleteById(anyLong());
		wishListRepo.deleteById(4L);
		verify(wishListRepo).deleteById(4L);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(foundWishList)));
	}
	
	@Test
	public void deleteProvidedCollectionOfWishListsFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(wishListRepo).deleteAll(anyCollection());
		wishListRepo.deleteAll(repository);
		verify(wishListRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteWishListsFromRepositoryByGivenIds() throws Exception {
		when(wishListRepo.findAllById(anyCollection())).thenReturn(setupRepository(wishList, wishList2));
		doAnswer(invocation -> {
			Iterable<WishList> wishListesToDelete = wishListRepo.findAllById(Arrays.asList(1L, 2L));
			wishListesToDelete.forEach(anAddress -> repository.remove(anAddress));
			return null;
		}).when(wishListRepo).deleteAllById(anyCollection());
		wishListRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(wishListRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(wishList)));
		assertThat(repository, not(hasItem(wishList2)));
	}
	
	@Test
	public void deleteProvidedWishListSuccessfully() throws Exception {
		when(wishListRepo.findById(anyLong())).thenReturn(Optional.of(wishList));
		WishList foundWishList = wishListRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(wishListRepo).delete(any(WishList.class));
		wishListRepo.delete(foundWishList);
		verify(wishListRepo).delete(foundWishList);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(wishList)));
	}

	@Test
	public void returnsWishListWhenAddedDateEquals() throws Exception {
		when(wishListRepo.findAll(any(Specification.class))).thenReturn(setupRepository(wishList));
		results = wishListRepo.findAll(new BtgSpecification<WishList> (new SearchCriteria("addedDate", SearchOperation.EQUALITY, "01/01/1970")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(wishList));
	}
	
	@Test
	public void returnsWishListWhenAddedDateBeginsWith() throws Exception {
		when(wishListRepo.findAll(any(Specification.class))).thenReturn(setupRepository(wishList, wishList2));
		results = wishListRepo.findAll(new BtgSpecification<WishList>(new SearchCriteria("addedDate", SearchOperation.STARTS_WITH, "01/01/197")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(wishList, wishList2));
	}
	
	@Test
	public void returnsWishListWhenAddedDateEndsWith() throws Exception {
		when(wishListRepo.findAll(any(Specification.class))).thenReturn(setupRepository(wishList));
		results = wishListRepo.findAll(new BtgSpecification<WishList>(new SearchCriteria("addedDate", SearchOperation.ENDS_WITH, "1970")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(wishList));
	}
	
	@Test
	public void returnsWishListWhenAddedDateContains() throws Exception {
		when(wishListRepo.findAll(any(Specification.class))).thenReturn(setupRepository(wishList2, wishList4));
		results = wishListRepo.findAll(new BtgSpecification<WishList>(new SearchCriteria("addedDate", SearchOperation.CONTAINS, "5")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(wishList2, wishList4));
	}
	
	@Test
	public void returnsWishListWhenAddedDateDoesntEqual() throws Exception {
		when(wishListRepo.findAll(any(Specification.class))).thenReturn(setupRepository(wishList, wishList2, wishList3, wishList4));
		results = wishListRepo.findAll(new BtgSpecification<WishList>(new SearchCriteria("addedDate", SearchOperation.NEGATION, "00/00/0000")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(wishList, wishList2, wishList3, wishList4));
	}
	
	@Test
	public void returnsWishListWhenAddedDateIsBefore() throws Exception {
		when(wishListRepo.findAll(any(Specification.class))).thenReturn(setupRepository(wishList));
		results = wishListRepo.findAll(new BtgSpecification<WishList>(new SearchCriteria("addedDate", SearchOperation.LESS_THAN, new Date(157680000000L))));
		assertThat(results.size(), is(1));
		assertThat(results, contains(wishList));
	}
	
	@Test
	public void returnsWishListWhenAddedDateIsAfter() throws Exception {
		when(wishListRepo.findAll(any(Specification.class))).thenReturn(setupRepository(wishList4));
		results = wishListRepo.findAll(new BtgSpecification<WishList>(new SearchCriteria("addedDate", SearchOperation.GREATER_THAN, new Date(315360000000L))));
		assertThat(results.size(), is(1));
		assertThat(results, contains(wishList4));
	}
	
	private String formatDateAsString(Date date) {
		return fmt.format(date);
	}
	
	private List<WishList> setupRepository(WishList...wishLists) {
		List<WishList> wishListList = new ArrayList<WishList>();
		for (WishList aWishList : wishLists) {
			wishListList.add(aWishList);
		}
		return wishListList;
	}
}