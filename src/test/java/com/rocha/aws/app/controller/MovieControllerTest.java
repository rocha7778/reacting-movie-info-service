package com.rocha.aws.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rocha.aws.app.service.MovieInfoService;

import reactor.test.StepVerifier;

@WebFluxTest(controllers = MovieController.class)
@AutoConfigureWebTestClient
public class MovieControllerTest {
	
	
	@Autowired
	private WebTestClient webClient;
	
	
	@MockBean
	private MovieInfoService movieInfoServiceMock;
	
	
	@Test
	void flux() {
		webClient.get().uri("/v1/flux").exchange().expectStatus().isOk()
		.expectBodyList(Integer.class)
		.hasSize(3);
	}
	
	@Test
	void fluxV2() {
		webClient.get().uri("/v1/flux").exchange().expectStatus().isOk()
		.expectBodyList(Integer.class)
		.consumeWith(result -> {
			var list = result.getResponseBody();
			assertEquals(1, list.get(0));
			assertEquals(2, list.get(1));
			assertEquals(3, list.get(2));
			
		})
		.hasSize(3);
	}
	
	@Test
	void fluxV3() {
		var flux = webClient.get().uri("/v1/flux").exchange().expectStatus().isOk()
		.returnResult(Integer.class)
		.getResponseBody();
		
		StepVerifier.create(flux)
		.expectNext(1)
		.expectNext(2)
		.expectNext(3)
		.verifyComplete();
	}
	
	@Test
	void stream() {
		var stream = webClient.get().uri("/v1/stream").exchange().expectStatus().isOk()
		.returnResult(Long.class)
		.getResponseBody();
		
		StepVerifier.create(stream)
		.expectNext(0L,1L,2L,3L)
		.thenCancel()
		.verify();
	}
}
