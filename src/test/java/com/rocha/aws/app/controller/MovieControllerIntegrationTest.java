package com.rocha.aws.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rocha.aws.app.domain.MovieInfo;
import com.rocha.aws.app.repository.MovieInfoRepository;


@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MovieControllerIntegrationTest {
	
	
	@Autowired
	private WebTestClient webClient;
	
	@Autowired
	MovieInfoRepository movieInfoRepository;
	
	private final String URL_MOVIES = "/v1/movies";

	@BeforeEach
	void setUp() {

		var movieinfos = List.of(
				new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
						LocalDate.parse("2005-06-15")),
				new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger"),
						LocalDate.parse("2008-07-18")),
				new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
						LocalDate.parse("2012-07-20")));
		movieInfoRepository.saveAll(movieinfos).blockLast();

	}

	@AfterEach
	void tearDown() {
		movieInfoRepository.deleteAll().block();
	}
	


	
	@Test
	void create() {
		
		var movie = new MovieInfo(null, "Batman Begins 2", 2007, List.of("Christian Bale", "Michael Cane"),
				LocalDate.parse("2005-06-15"));
		
		webClient.post().uri(URL_MOVIES)
		.bodyValue(movie)
		.exchange()
		.expectStatus()
		.isCreated()
		.expectBody(MovieInfo.class)
		.consumeWith(e -> {
			var savedMovie = e.getResponseBody();
			assertNotNull(savedMovie.getId());
		});
		
	}
	
	@Test
	void findAll() {
		
		webClient.get().uri(URL_MOVIES)
		.exchange()
		.expectStatus()
		.isOk()
		.expectBodyList(MovieInfo.class)
		.hasSize(3)
		;
		
	}
	
	@Test
	void findById() {
		var movieId = "abc";
		webClient.get().uri(URL_MOVIES+"/{id}", movieId)
		.exchange()
		.expectStatus()
		.isOk()
		.expectBody(MovieInfo.class)
		.consumeWith(e -> {
			var bodyResponse = e.getResponseBody();
			assertThat(bodyResponse.getName()).isEqualTo("Dark Knight Rises");
		})
		;
	}
	
	@Test
	void findByIdNotFound() {
		var movieId = "dfe";
		webClient.get().uri(URL_MOVIES+"/{id}", movieId)
		.exchange()
		.expectStatus()
		.isNotFound();
	}
	
	
	@Test
	void update() {
		
		var movieId = "abc";
		var movie = new MovieInfo("abc", "Dark Knight Rises 2", 2012, List.of("Christian Bale", "Tom Hardy"),
				LocalDate.parse("2012-07-21"));
		
		webClient.put().uri(URL_MOVIES+"/{id}",movieId)
		.bodyValue(movie)
		.exchange()
		.expectStatus()
		.isOk()
		.expectBody(MovieInfo.class)
		.consumeWith(e -> {
			var updatedMovie = e.getResponseBody();
			assertNotNull(updatedMovie.getId());
			assertThat(updatedMovie.getName()).isEqualTo("Dark Knight Rises 2");
			assertThat(updatedMovie.getReleaseDate()).isEqualTo(LocalDate.parse("2012-07-21"));
		});
		
	}
	
	@Test
	void deleteById() {
		
		var movieId = "abc";
		webClient.delete().uri(URL_MOVIES+"/{id}",movieId)
		.exchange()
		.expectStatus()
		.isNoContent();
		
		
		webClient.get().uri(URL_MOVIES+"/{id}", movieId)
		.exchange()
		.expectStatus()
		.isNotFound()
		.expectBody(MovieInfo.class)
		.consumeWith(e -> {
			var bodyResponse = e.getResponseBody();
			assertNull(bodyResponse);
		})
		;
		
	}

}
