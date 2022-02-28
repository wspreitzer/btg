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

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.btg.website.WebsiteApplication;
import com.btg.website.config.JacksonConfig;
import com.btg.website.model.Product;
import com.btg.website.repository.ProductRepository;
import com.btg.website.util.TestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JacksonConfig.class})
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment=WebEnvironment.MOCK, classes = {WebsiteApplication.class})
@SuppressWarnings("unchecked")
public class ProductRestControllerTest {

	@MockBean private ProductRepository productRepo;

	@InjectMocks ProductRestController controller;
	
	@Autowired private WebApplicationContext webApplicationContext;
	
	
	private MockMvc mockedRequest;
	private TestUtils<Product> productUtils;
	private Product product, product2, product3, product4;
	private List<Product> productList;
	
	@BeforeEach
	private void setup() {
		productUtils = new TestUtils<Product>();
		product = new Product("BTG Baseball Hat", "5486-58548-LG", "Red Fitted Baseball hat size large", 60, 19.95);
		product2 = new Product("BTG Baseball Jersey", "5486-58550-SM", "Red Baseball Jersey size small", 65, 69.95);
		product3 = new Product("BTG Hockey Jersey", "5486-58551-XL", "Red Hockey Jersey size Xtra large", 55, 129.95);
		product4 = new Product("BTG Mousepad", "5486-2345-MP", "Mouse pad", 45, 12.95);
		productList = productUtils.setupRepository(product, product2, product3, product4);
		this.mockedRequest = webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void createsProductWhenRequestIsVaild() throws Exception {
		Product productToSave = new Product("New Product","SKU","This is a new product",15, 19.95);
		when(productRepo.save(any(Product.class))).thenReturn(productToSave);
		MvcResult mvcResult = mockedRequest
				.perform(post("/btg/admin/rest/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\" : \"New Product\", \"sku\" : \"SKU\", \"description\" : \"This is a new product\", \"qty\" : \"15\", \"price\" : \"19.95\"}")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("New Product"))
				.andExpect(jsonPath("$.sku").value("SKU"))
				.andExpect(jsonPath("$.description").value("This is a new product"))
				.andExpect(jsonPath("$.qty").value(Integer.valueOf("15")))
				.andExpect(jsonPath("$.price").value(Double.valueOf("19.95")))
				.andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returns404WhenProductIsNotFoundById() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.empty());
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/product/0")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsAllProductsWhenNoCriteriaIsProvided() throws Exception {
		when(productRepo.findAll()).thenReturn(productList);
		MvcResult mvcResult = mockedRequest.perform(get("/btg/rest/products/")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenIdIsFound() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
		Product product = productRepo.findById(1L).get();
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/product/1")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.name").value(product.getName()))
						.andExpect(jsonPath("$.sku").value(product.getSku()))
						.andExpect(jsonPath("$.description").value(product.getDescription()))
						.andExpect(jsonPath("$.qty").value(product.getQty()))
						.andExpect(jsonPath("$.price").value(product.getPrice())).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsProductWhenNameEquals() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch/")
						.param("search", "name:BTG Baseball Hat")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void returnsProductWhenNameEquals2() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch/")
						.param("search", "name:BTG Baseball Hat")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenNameContains() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch/")
						.param("search", "name:*Mou*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenNameDoesNotEqual() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product2, product3, product4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch/")
						.param("search", "name!BTG Baseball Hat")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenSkuBeginsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product, product2, product3, product4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch/")
						.param("search", "sku:5486*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenSkuContains() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product2, product3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch/")
						.param("search", "sku:*55*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenSkuDoesNotEqual() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product2, product3, product4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch/")
						.param("search", "sku!5486-58548-LG")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
		
	@Test
	public void returnsProductWhenDescriptionBeginsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch")
						.param("search", "description:Red Hockey")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenDescriptionEndsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product, product3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch")
						.param("search", "description:*large")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void returnsProductWhenQtyEquals() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch")
						.param("search", "qty:60")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenQtyBeginsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product, product2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch")
						.param("search", "qty:6*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenQtyEndsWith() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch")
						.param("search", "qty:*0")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenQtyDoesNotEqual() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product2, product3, product4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch")
						.param("search", "qty!45")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
		
	}
	
	@Test
	public void returnsProductWhenQtyLessThan() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product3, product4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch")
						.param("search", "qty<56")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenQtyIsGreaterThan() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product, product2));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch")
						.param("search", "qty>56")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
			
	@Test
	public void returnsProductWhenPriceContains() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product, product3, product4));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch")
						.param("search", "price:*1*")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenPriceDoesNotEqual() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product, product2, product3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch")
						.param("search", "price!12.95")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void returnsProductWhenPriceIsGreaterThan() throws Exception {
		when(productRepo.findAll(any(Specification.class))).thenReturn(productUtils.setupRepository(product2, product3));
		MvcResult mvcResult = mockedRequest
				.perform(get("/btg/rest/productSearch")
						.param("search", "price>20.00")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void updatesProductByIdWhenRequestIsValid() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
		when(productRepo.save(any(Product.class))).thenReturn(product);
		MvcResult mvcResult = mockedRequest
				.perform(put("/btg/admin/rest/updateProduct/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\" : \"Btg Baseball Cap\", \"sku\" : \"5486-58548-SM\", \"description\" : \"Red Fitted Baseball cap size small\", \"qty\" : \"100\", \"price\" : \"22.95\" }")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Btg Baseball Cap"))
				.andExpect(jsonPath("$.sku").value("5486-58548-SM"))
				.andExpect(jsonPath("$.description").value("Red Fitted Baseball cap size small"))
				.andExpect(jsonPath("$.qty").value(Integer.valueOf("100")))
				.andExpect(jsonPath("$.price").value(Double.valueOf("22.95"))).andReturn();
		Product foundProduct = productRepo.findById(1L).get();
		assertThat(foundProduct.getName(), is("Btg Baseball Cap"));
		assertThat(foundProduct.getSku(), is("5486-58548-SM"));
		assertThat(foundProduct.getDescription(), is("Red Fitted Baseball cap size small"));
		assertThat(foundProduct.getQty(), is(Integer.valueOf("100")));
		assertThat(foundProduct.getPrice(), is(Double.valueOf("22.95")));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesProductNameWhenRequestIsValid() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
		when(productRepo.save(any(Product.class))).thenReturn(product);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/admin/rest/updateProductField/1")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/name\", \"value\" : \"Btg Baseball Cap\" }]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		product.setName("Btg Baseball Cap");
		Product foundProduct = productRepo.findById(1L).get();
		assertThat(foundProduct.getName(), is("Btg Baseball Cap"));
		assertThat(foundProduct.getSku(), is(product.getSku()));
		assertThat(foundProduct.getDescription(), is(product.getDescription()));
		assertThat(foundProduct.getQty(), is(product.getQty()));
		assertThat(foundProduct.getPrice(), is(product.getPrice()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void updatesProductSkuWhenRequestIsValid() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
		when(productRepo.save(any(Product.class))).thenReturn(product);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/admin/rest/updateProductField/1")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/sku\", \"value\" : \"5486-58548-SM\" }]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		product.setSku("5486-58548-SM");
		Product foundProduct = productRepo.findById(1L).get();
		assertThat(foundProduct.getName(), is(product.getName()));
		assertThat(foundProduct.getSku(), is("5486-58548-SM"));
		assertThat(foundProduct.getDescription(), is(product.getDescription()));
		assertThat(foundProduct.getQty(), is(product.getQty()));
		assertThat(foundProduct.getPrice(), is(product.getPrice()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesProductDescriptionWhenRequestIsValid() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
		when(productRepo.save(any(Product.class))).thenReturn(product);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/admin/rest/updateProductField/1")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/description\", \"value\" : \"Red Fitted Baseball cap size small\" }]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		product.setDescription("Red Fitted Baseball cap size small");
		Product foundProduct = productRepo.findById(1L).get();
		assertThat(foundProduct.getName(), is(product.getName()));
		assertThat(foundProduct.getSku(), is(product.getSku()));
		assertThat(foundProduct.getDescription(), is("Red Fitted Baseball cap size small"));
		assertThat(foundProduct.getQty(), is(product.getQty()));
		assertThat(foundProduct.getPrice(), is(product.getPrice()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesProductQtyWhenRequestIsValid() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
		when(productRepo.save(any(Product.class))).thenReturn(product);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/admin/rest/updateProductField/1")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/qty\", \"value\" : \"100\" }]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		product.setQty(100);
		Product foundProduct = productRepo.findById(1L).get();
		assertThat(foundProduct.getName(), is(product.getName()));
		assertThat(foundProduct.getSku(), is(product.getSku()));
		assertThat(foundProduct.getDescription(), is(product.getDescription()));
		assertThat(foundProduct.getQty(), is(Integer.valueOf("100")));
		assertThat(foundProduct.getPrice(), is(product.getPrice()));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void updatesProductPriceWhenRequestIsValid() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
		when(productRepo.save(any(Product.class))).thenReturn(product);
		MvcResult mvcResult = mockedRequest
				.perform(patch("/btg/admin/rest/updateProductField/1")
						.contentType("application/json-patch+json")
						.content("[{\"op\" : \"replace\", \"path\" : \"/price\", \"value\" : \"22.95\" }]")
						.accept("application/json-patch+json"))
				.andExpect(status().isOk()).andReturn();
		product.setPrice(22.95);
		Product foundProduct = productRepo.findById(1L).get();
		assertThat(foundProduct.getName(), is(product.getName()));
		assertThat(foundProduct.getSku(), is(product.getSku()));
		assertThat(foundProduct.getDescription(), is(product.getDescription()));
		assertThat(foundProduct.getQty(), is(product.getQty()));
		assertThat(foundProduct.getPrice(), is(Double.valueOf("22.95")));
		System.out.println(mvcResult.getResponse().getContentAsString());
	}
	
	@Test
	public void deletesProductByIdWhenRequestIsValid() throws Exception {
		when(productRepo.findById(anyLong())).thenReturn(Optional.of(product));
		Product foundProduct = productRepo.findById(1L).get();
		doAnswer(invocation -> {
			productList.remove(0);
			return null;
		}).when(productRepo).deleteById(anyLong());
		mockedRequest
			.perform(delete("/btg/admin/rest/deleteProduct/1"))
			.andExpect(status().isNoContent()).andReturn();
		verify(productRepo).deleteById(1L);
		assertThat(productList.size(), is(3));
		assertThat(productList, not(hasItem(foundProduct)));
	}
	
	@Test
	public void deletesAllProducts() throws Exception {
		doAnswer(invocation -> {
			productList.clear();
			return null;
		}).when(productRepo).deleteAll();
		mockedRequest
		.perform(delete("/btg/admin/rest/deleteProducts"))
		.andExpect(status().isNoContent()).andReturn();
		verify(productRepo).deleteAll();
		assertThat(productList.size(), is(0));
	}
}