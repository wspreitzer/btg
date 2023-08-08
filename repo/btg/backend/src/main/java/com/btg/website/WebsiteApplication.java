package com.btg.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsiteApplication.class, args);
	}
	
//	@Bean
//	public CommandLineRunner specificationsDemo(MovieRepository repo) {
//		return args -> {
//			repo.saveAll(Arrays.asList(
//				new Movie("Troy", "Drama", 7.2, 106, 2004),
//				new Movie("The Godfather", "Crime", 9.2, 178, 1972),
//				new Movie("Invictus", "Sport", 7.3, 135, 2009),
//				new Movie("Black Panther", "Action", 7.3, 135, 2018),
//				new Movie("Joker", "Drama", 8.9, 122, 2018),
//				new Movie("Iron Man", "Action", 8.9, 126, 2008)
//			));
//			BtgSpecificationBuilder<Movie> builder = new BtgSpecificationBuilder<>();
//			builder = BtgUtils.buildSearchCriteria(builder, "title~*Godf*");
//			Specification<Movie> spec = builder.build(searchCriteria -> new BtgSpecification<Movie>((SearchCriteria) searchCriteria));
//			List<Movie> movieList = repo.findAll(spec);
//			movieList.stream().forEach(System.out::println);
//			
//			System.out.println();
//			BtgSpecificationBuilder<Movie> builder2 = new BtgSpecificationBuilder<>();
//			builder2 = BtgUtils.buildSearchCriteria(builder2, "genre:Action,'genre:Drama");
//			Specification<Movie> spec2 = builder2.build(searchCriteria -> new BtgSpecification<Movie>((SearchCriteria) searchCriteria));
//			List<Movie> movieList2 = repo.findAll(spec2);
//			movieList2.stream().forEach(System.out::println);
			
//			System.out.println();
//			BtgSpecificationBuilder<Movie> builder3 = new BtgSpecificationBuilder<>();
//			builder3 = BtgUtils.buildSearchCriteria(builder3, "rating:8.9");
//			Specification<Movie> spec3 = builder3.build(searchCriteria -> new BtgSpecification<Movie>((SearchCriteria) searchCriteria));
//			List<Movie> movieList3 = repo.findAll(spec3);
//			movieList3.stream().forEach(System.out::println);
//			
//			System.out.println();
//			BtgSpecificationBuilder<Movie> builder4 = new BtgSpecificationBuilder<>();
//			builder4 = BtgUtils.buildSearchCriteria(builder4, "rating<=8.9");
//			Specification<Movie> spec4 = builder4.build(searchCriteria -> new BtgSpecification<Movie>((SearchCriteria) searchCriteria));
//			List<Movie> movieList4 = repo.findAll(spec4);
//			movieList4.stream().forEach(System.out::println);
			
//			System.out.println();
//			
//			
//			System.exit(0);
//					
//		};
//	}

}
