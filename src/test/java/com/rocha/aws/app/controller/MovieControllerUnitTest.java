package com.rocha.aws.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rocha.aws.app.domain.MovieInfo;
import com.rocha.aws.app.service.MovieInfoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = MovieController.class)
@AutoConfigureWebTestClient
public class MovieControllerUnitTest {

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private MovieInfoService movieInfoServiceMock;

	private final String URL_MOVIES = "/v1/movies";

	@Test
	void getAllMoviesIfno() {
		var movieinfos = List.of(
				new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
						LocalDate.parse("2005-06-15")),
				new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Heath Ledger"),
						LocalDate.parse("2008-07-18")),
				new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
						LocalDate.parse("2012-07-20")));

		when(movieInfoServiceMock.findAll())
		.thenReturn(Flux.fromIterable(movieinfos));

		webClient.get()
		.uri(URL_MOVIES)
		.exchange().expectStatus()
		.isOk()
		.expectBodyList(MovieInfo.class)
		.hasSize(3);
	}
	
	
	@Test
	void create() {
		
		var movie = new MovieInfo(null, "Batman Begins 2", 2007, List.of("Christian Bale", "Michael Cane"),
				LocalDate.parse("2005-06-15"));
		var responseMock = movie.clone();
		responseMock.setId("abc");
		
		when(movieInfoServiceMock.create(movie)).thenReturn(Mono.just(responseMock));
		
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
	void createMovieBadRequest() {
		
		var movie = new MovieInfo(null, " ", null, List.of("Christian Bale", "Michael Cane"),
				LocalDate.parse("2005-06-15"));
		var responseMock = movie.clone();
		responseMock.setId("abc");
		
		
		webClient.post().uri(URL_MOVIES)
		.bodyValue(movie)
		.exchange()
		.expectStatus()
		.isBadRequest()
		.expectBody(String.class)
		.consumeWith(e -> {
		var responseBoy = e.getResponseBody();
		assertEquals("movieInfo.name must be present,movieInfo.year must be present", responseBoy);
		});
		
		
	}
	
	@Test
	void findById() {
		
		var movie = new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
				LocalDate.parse("2012-07-20"));
		
		var movieId = "abc";
		when(movieInfoServiceMock.findById(movieId)).thenReturn(Mono.just(movie));
		
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
	void update() {
		
		var movieId = "abc";
		var movie = new MovieInfo("abc", "Dark Knight Rises 2", 2012, List.of("Christian Bale", "Tom Hardy"),
				LocalDate.parse("2012-07-21"));
		
		when(movieInfoServiceMock.update(movie, movieId)).thenReturn(Mono.just(movie));
		
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
		
		when(movieInfoServiceMock.deleteById(movieId)).thenReturn(Mono.empty());
		when(movieInfoServiceMock.findById(movieId)).thenReturn(Mono.empty());
		
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
