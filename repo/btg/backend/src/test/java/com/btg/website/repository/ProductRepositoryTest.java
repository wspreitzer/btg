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

import com.btg.website.model.Product;
import com.btg.website.repository.specification.BtgSpecification;
import com.btg.website.util.SearchCriteria;
import com.btg.website.util.SearchOperation;

public class ProductRepositoryTest {

	@MockBean
	private ProductRepository productRepo;

	private Product product, product2, product3, product4;
	
	private List<Product> repository;
	private List<Product> results;
	
	@BeforeEach
	public void setup() {
		productRepo = mock(ProductRepository.class);
		product = new Product("BTG Baseball Hat", "5486-58548-LG", "Red Fitted Baseball hat size large", 60, 19.95);
		product2 = new Product("BTG Baseball Jersey", "5486-58550-SM", "Red Baseball Jersey size small", 65, 69.95);
		product3 = new Product("BTG Hockey Jersey", "5486-58551-XL", "Red Hockey Jersey size Xtra large", 55, 129.95);
		product4 = new Product("BTG Mousepad", "5486-2345-MP", "Mouse pad", 45, 12.95);
		repository = setupRepository(product, product2, product3, product4);
	}
	
	@Test
	public void returnsNoResultsWhenIdIsNotFound() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.empty());
		Optional<Product> emptyProduct = productRepo.findById(-1L);
		assertThat(false, is(emptyProduct.isPresent()));
	}
	
	@Test
	public void returnsAllProductsWhenNoSearchCriteriaIsProvided() throws Exception {
		when(productRepo.findAll()).thenReturn(repository);
		List<Product> returnedProductes = (List<Product>) productRepo.findAll();
		assertThat(returnedProductes.size(), is(4));
		assertThat(returnedProductes, containsInAnyOrder(product, product2, product3, product4));
	}
	
	@Test
	public void returnsProductWhenIdIsFound() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
		Optional<Product> foundProduct = productRepo.findById(1L);
		assertThat(true, is(foundProduct.isPresent()));
		assertThat(foundProduct.get().getName(), is("BTG Baseball Hat"));
		assertThat(foundProduct.get().getSku(), is("5486-58548-LG"));
		assertThat(foundProduct.get().getDescription(), is("Red Fitted Baseball hat size large"));
		assertThat(foundProduct.get().getQty(), is(60));
		assertThat(foundProduct.get().getPrice(), is(19.95));
	}
	
	@Test
	public void savesProductToRepositorySuccessfully() throws Exception {
		Product productToSave = new Product("BTG Tee Shirt","5486-58547-LG", "Red T-Shirt size Large", 100, 25.95);
		when(productRepo.save(any(Product.class))).thenReturn(productToSave);
		Product newProduct = productRepo.save(productToSave);
		assertThat(newProduct.getName(), is("BTG Tee Shirt"));
		assertThat(newProduct.getSku(), is("5486-58547-LG"));
		assertThat(newProduct.getDescription(), is("Red T-Shirt size Large"));
		assertThat(newProduct.getQty(), is(100));
		assertThat(newProduct.getPrice(), is(25.95));
	}
	
	@Test
	public void savesMutipleProductToRepositorySuccessfully() throws Exception {
		List<Product> listOfProductsToSave = new ArrayList<Product>();
		Product productToSave = new Product("BTG Tee Shirt", "5486-58547-LG", "Red T-Shirt size Large", 100, 25.95);
		Product productToSave2 = new Product("BTG Tee Shirt", "5486-58547-SM", "Red T-Shirt size Small", 100, 25.95);
		
		listOfProductsToSave.add(productToSave);
		listOfProductsToSave.add(productToSave2);
		when(productRepo.saveAll(anyCollection())).thenReturn(listOfProductsToSave);
		List<Product> savedProducts = productRepo.saveAll(listOfProductsToSave);
		assertThat(savedProducts.size(), is(2));
		assertThat(savedProducts, containsInAnyOrder(productToSave2, productToSave));
	}
	
	@Test
	public void returnsTheRecordCountOfTheRepository() throws Exception {
		when(productRepo.count()).thenReturn((long) repository.size());
		long count = productRepo.count();
		assertThat(count, is(4L));
	}

	@Test
	public void deleteEntireRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(productRepo).deleteAll();
		productRepo.deleteAll();
		verify(productRepo).deleteAll();
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteProductFromRepositoryById() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.of(product4));
		Product foundProduct = productRepo.findById(4L).get();
		doAnswer(invocation -> {
			repository.remove(3);
			return null;
		}).when(productRepo).deleteById(anyLong());
		productRepo.deleteById(4L);
		verify(productRepo).deleteById(4L);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(foundProduct)));
	}
	
	@Test
	public void deleteProvidedCollectionOfProductsFromRepositorySuccessfully() throws Exception {
		doAnswer(invocation -> {
			repository.clear();
			return null;
		}).when(productRepo).deleteAll(anyCollection());
		productRepo.deleteAll(repository);
		verify(productRepo).deleteAll(repository);
		assertThat(repository.size(), is(0));
	}

	@Test
	public void deleteProductsFromRepositoryByGivenIds() throws Exception {
		when(productRepo.findAllById(anyCollection())).thenReturn(setupRepository(product, product2));
		doAnswer(invocation -> {
			Iterable<Product> productesToDelete = productRepo.findAllById(Arrays.asList(1L, 2L));
			productesToDelete.forEach(anAddress -> repository.remove(anAddress));
			return null;
		}).when(productRepo).deleteAllById(anyCollection());
		productRepo.deleteAllById(Arrays.asList(1L, 2L));
		verify(productRepo).deleteAllById(Arrays.asList(1L, 2L));
		assertThat(repository.size(), is(2));
		assertThat(repository, not(hasItem(product)));
		assertThat(repository, not(hasItem(product2)));
	}
	
	@Test
	public void deleteProvidedProductSuccessfully() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
		Product foundProduct = productRepo.findById(1L).get();
		doAnswer(invocation -> {
			repository.remove(0);
			return null;
		}).when(productRepo).delete(any(Product.class));
		productRepo.delete(foundProduct);
		verify(productRepo).delete(foundProduct);
		assertThat(repository.size(), is(3));
		assertThat(repository, not(hasItem(product)));
	}
	
	@Test
	public void returnsProductWhenNameEquals() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product));
		results = productRepo.findAll(new BtgSpecification<Product> (new SearchCriteria("name", SearchOperation.EQUALITY, "BTG Baseball Hat")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(product));
	}
	
	@Test
	public void returnsProductWhenNameBeginsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product, product2, product3, product4));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("name", SearchOperation.STARTS_WITH, "BTG")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(product, product2, product3, product4));
	}
	
	@Test
	public void returnsProductWhenNameEndsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("name", SearchOperation.ENDS_WITH, "Hat")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(product));
	}

	@Test
	public void returnsProductWhenNameContains() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product4));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("name", SearchOperation.CONTAINS, "Mou")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(product4));
	}
	
	@Test
	public void returnsProductWhenNameDoesntEqual() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product2, product3, product4));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("name", SearchOperation.NEGATION, "BTG Baseball Hat")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(product2, product3, product4));
	}
	
	@Test
	public void returnsProductWhenSkuEquals() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product4));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("sku", SearchOperation.EQUALITY, "5486-2345-MP")));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(product4));
	}
	
	@Test
	public void returnsProductWhenSkuBeginsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product, product2, product3, product4));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("sku", SearchOperation.STARTS_WITH, "5486")));
		assertThat(results.size(), is(4));
		assertThat(results, contains(product, product2, product3, product4));
	}
	
	@Test
	public void returnsProductWhenSkuEndsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product3));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("sku", SearchOperation.ENDS_WITH, "-XL")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(product3));
	}
	
	@Test
	public void returnsProductWhenSkuContains() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product2, product3));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("sku", SearchOperation.CONTAINS, "55")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(product2, product3));
	}
	
	@Test
	public void returnsProductWhenSkuDoesntEqual() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product2, product3, product4));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("sku", SearchOperation.NEGATION, "5486-58548-LG")));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(product2, product3, product4));
	}
	
	@Test
	public void returnsProductWhenDescriptionEquals() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("description", SearchOperation.EQUALITY, "Red Fitted Baseball hat size large")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(product));
	}
	
	@Test
	public void returnsProductWhenDescriptionBeginsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product3));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("description", SearchOperation.STARTS_WITH, "Red Hockey")));
		assertThat(results.size(), is(1));
		assertThat(results, contains(product3));
	}
	
	@Test
	public void returnsProductWhenDescriptionEndsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product, product3));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("description", SearchOperation.ENDS_WITH, "large")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(product, product3));
	}
	
	@Test
	public void returnsProductWhenDescriptionContains() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product3, product2));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("description", SearchOperation.CONTAINS, "Jersey")));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(product3, product2));
	}
	
	@Test
	public void returnsProductWhenDescriptionDoesntEqual() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product, product2, product3, product4));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("description", SearchOperation.NEGATION, "This is not a product")));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(product, product2, product3, product4));
	}
	
	@Test
	public void returnsProductWhenQtyEquals() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("qty", SearchOperation.EQUALITY, 60)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(product));
	}
	
	@Test
	public void returnsProductWhenQtyBeginsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product2, product));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("qty", SearchOperation.STARTS_WITH, 6)));
		assertThat(results.size(), is(2));
		assertThat(results, containsInAnyOrder(product2, product));
	}
	
	@Test
	public void returnsProductWhenQtyEndsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("qty", SearchOperation.ENDS_WITH, 0)));
		assertThat(results.size(), is(1));
		assertThat(results, containsInAnyOrder(product));
	}
	
	@Test
	public void returnsProductWhenQtyContains() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product2, product3, product4));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("qty", SearchOperation.CONTAINS, 5)));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(product2, product3, product4));
	}
	
	@Test
	public void returnsProductWhenQtyDoesntEqual() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product2, product3, product));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("qty", SearchOperation.NEGATION, 45)));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(product2, product3, product));
	}
	
	@Test
	public void returnsProductWhenPriceEquals() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product3));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("price", SearchOperation.EQUALITY, 129.95)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(product3));
	}
	
	@Test
	public void returnsProductWhenPriceBeginsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product2));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("price", SearchOperation.STARTS_WITH, 6)));
		assertThat(results.size(), is(1));
		assertThat(results, contains(product2));
	}
	
	@Test
	public void returnsProductWhenPriceEndsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product, product2, product3, product4));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("price", SearchOperation.ENDS_WITH, .95)));
		assertThat(results.size(), is(4));
		assertThat(results, containsInAnyOrder(product, product2, product3, product4));
	}
	
	@Test
	public void returnsProductWhenPriceContains() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product, product3, product4));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("price", SearchOperation.CONTAINS, 1)));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(product, product3, product4));
	}
	
	@Test
	public void returnsProductWhenPriceDoesntEqual() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(setupRepository(product2, product3, product));
		results = productRepo.findAll(new BtgSpecification<Product>(new SearchCriteria("price", SearchOperation.NEGATION, 12.95)));
		assertThat(results.size(), is(3));
		assertThat(results, containsInAnyOrder(product2, product3, product));
	}
	
	private List<Product> setupRepository(Product...products) {
		List<Product> productList = new ArrayList<Product>();
		for (Product aProduct : products) {
			productList.add(aProduct);
		}
		return productList;
	}
}