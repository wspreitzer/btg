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

import com.btg.website.model.Review;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

@SuppressWarnings("unchecked")
public class ReviewRepositoryTest {

	@MockBean
	private ReviewRepository reviewRepo;
	
	private Review review, review2, review3, review4;
	
	private List<Review> repository;
	
	private List<Review> results;
	
	@BeforeEach
	public void setup() {
		reviewRepo = mock(ReviewRepository.class);
		review = new Review(null, "This Place is Awesome", new Date(System.currentTimeMillis()));
		review2 = new Review(null, "This Joint is Good", new Date(System.currentTimeMillis()));
		review3 = new Review(null, "This Joint is Awesome", new Date(System.currentTimeMillis()));
		review4 = new Review(null, "This Place is Bad", new Date(System.currentTimeMillis()));
		repository = setupRepository(review, review2, review3, review4);
	}

	@Test
	public void returnsNoResultsWhenIdIsNotFound() throws Exception {
		when(reviewRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<Review> emptyReview = reviewRepo.findById(-1L);
		assertThat(false, is(emptyReview.isPresent()));
	}
	
	@Test
	public void returnsAllReviewsWhenNoSearchCriteriaIsProvided() throws Exception {
		when(reviewRepo.findAll()).thenReturn(repository);
		List<Review> returnedReviews = (List<Review>) reviewRepo.findAll();
		assertThat(returnedReviews.size(), is(4));
		assertThat(returnedReviews, containsInAnyOrder(review, review2, review3, review4));
	}

	@Test
	public void returnsReviewWhenIdIsFound() throws Exception {
		when(reviewRepo.findById(anyLong())).thenReturn(Optional.of(review));
		Optional<Review> foundReview = reviewRepo.findById(1L);
		assertThat(true, is(foundReview.isPresent()));
		assertThat(foundReview.get().getReview(), is("This Place is Awesome"));
	}
	
	@Test
	public void savesReviewToRepositorySuccessfully() throws Exception {
		Review reviewToSave = new Review(null, "Blah", new Date(System.currentTimeMillis()));
		when(reviewRepo.save(any(Review.class))).thenReturn(reviewToSave);
		Review newReview = reviewRepo.save(reviewToSave);
		assertThat(newReview.getReview(), is("Blah"));
	}

	@Test
	public void savesMutipleReviewsToRepositorySuccessfully() throws Exception {
		List<Review> listOfReviewsToSave = new ArrayList<Review>();
		Review reviewToSave = new Review(null, "New Review", new Date(System.currentTimeMillis()));
		Review reviewToSave2 = new Review(null, "New Review2", new Date(System.currentTimeMillis()));
		
		listOfReviewsToSave.add(reviewToSave);
		listOfReviewsToSave.add(reviewToSave2);
		when(reviewRepo.saveAll(anyCollection())).thenReturn(listOfReviewsToSave);
		List<Review> savedReviews = reviewRepo.saveAll(listOfReviewsToSave);
		assertThat(savedReviews.size(), is(2));
		assertThat(savedReviews, containsInAnyOrder(reviewToSave2, reviewToSave));
	}
	
	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(reviewRepo.count()).thenReturn((long) repository.size());
		long count = reviewRepo.count();
		assertThat(count, is(4L));
	}
	
	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(reviewRepo).deleteAll();
		reviewRepo.deleteAll();
		verify(reviewRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}
	
	@Test
	public void deleteReviewFromRepositoryById() throws Exception {
		when(reviewRepo.findById(anyLong())).thenReturn(Optional.of(review4));
		Review foundReview = reviewRepo.findById(4L).get();
		doAnswer(invocation -> {
			repository.remove(3);
			return null;
		}).when(reviewRepo).deleteById(anyLong());
		reviewRepo.deleteById(4L);
		verify(reviewRepo).deleteById(4L);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(foundReview)));
	}

	@Test
	public void deleteProvidedCollectionOfReviewsFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(reviewRepo).deleteAll(anyCollection());
		reviewRepo.deleteAll(repository);
		verify(reviewRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}
	
	@Test
	public void deleteReviewsFromRepositoryByGivenIds() throws Exception {
		when(reviewRepo.findAllById(anyCollection())).thenReturn(setupRepository(review, review2));
		doAnswer(invocation -> {
			Iterable<Review> reviewesToDelete = reviewRepo.findAllById(Arrays.asList(1L, 2L));
			reviewesToDelete.forEach(aReview -> repository.remove(aReview));
			return null;
		}).when(reviewRepo).deleteAllById(anyCollection());
		reviewRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(reviewRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(review)));
		assertThat(repository, not(hasItem(review2)));
	}
	
	@Test
	public void deleteProvidedReviewSuccessfully() throws Exception {
		when(reviewRepo.findById(anyLong())).thenReturn(Optional.of(review));
		Review foundReview = reviewRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(reviewRepo).delete(any(Review.class));
		reviewRepo.delete(foundReview);
		verify(reviewRepo).delete(foundReview);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(review)));
	}
	
	@Test
	public void returnsReviewWhenReviewEquals() throws Exception {
		when(reviewRepo.findAll(any(Specification.class))).thenReturn(setupRepository(review));
		results = reviewRepo.findAll(new BtgSpecification<Review>(new SearchCriteria("review", SearchOperation.EQUALITY, "This Place is Awesome")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(review));
	}
	
	@Test
	public void returnsReviewWhenReviewBeginsWith() throws Exception {
		when(reviewRepo.findAll(any(Specification.class))).thenReturn(setupRepository(review2, review3));
		results = reviewRepo.findAll(new BtgSpecification<Review>(new SearchCriteria("review", SearchOperation.STARTS_WITH, "This Joint")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(review2, review3));
	}
	
	@Test
	public void returnsReviewWhenReviewEndsWith() throws Exception {
		when(reviewRepo.findAll(any(Specification.class))).thenReturn(setupRepository(review, review3));
		results = reviewRepo.findAll(new BtgSpecification<Review>(new SearchCriteria("review", SearchOperation.ENDS_WITH, "Awesome")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(review, review3));
	}
	
	@Test
	public void returnsReviewWhenReviewContains() throws Exception {
		when(reviewRepo.findAll(any(Specification.class))).thenReturn(setupRepository(review4));
		results = reviewRepo.findAll(new BtgSpecification<Review>(new SearchCriteria("review", SearchOperation.CONTAINS, "Bad")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(review4));
	}
	
	@Test
	public void returnsReviewWhenReviewDoesntEquals() throws Exception {
		when(reviewRepo.findAll(any(Specification.class))).thenReturn(repository);
		results = reviewRepo.findAll(new BtgSpecification<Review>(new SearchCriteria("review", SearchOperation.NEGATION, "This Place is Sweet")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(review, review2, review3, review4));
	}
	
	private List<Review> setupRepository(Review...reviews) {
		List<Review> reviewList = new ArrayList<Review>();
		for (Review aReview : reviews) {
			reviewList.add(aReview);
		}
		return reviewList;
	}
}