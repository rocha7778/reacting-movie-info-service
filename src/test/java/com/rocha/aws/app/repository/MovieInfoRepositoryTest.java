package com.rocha.aws.app.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import com.rocha.aws.app.domain.MovieInfo;

import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {

	@Autowired
	MovieInfoRepository movieInfoRepository;

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
	void findAll() {
		var moviesList = movieInfoRepository.findAll();
		StepVerifier.create(moviesList).expectNextCount(3).verifyComplete();
	}

	@Test
	void findById() {

		var movie = movieInfoRepository.findById("abc").log();

		StepVerifier.create(movie).consumeNextWith(e -> {
			assertEquals("Dark Knight Rises", e.getName());
		}).verifyComplete();
	}

	@Test
	void saveMovie() {

		var movie = new MovieInfo(null, "Dark Rocha Night", 2012, List.of("Christian Bale", "Tom Hardy"),
				LocalDate.parse("2020-07-20"));

		var result = movieInfoRepository.save(movie);

		StepVerifier.create(result).consumeNextWith(e -> {
			assertNotNull(e.getId());
		}).verifyComplete();

	}
	
	
	@Test
	void updateMovie() {

		var movie = movieInfoRepository.findById("abc").block();
		
		movie.setName("Rocha Dark Night");

		var result = movieInfoRepository.save(movie);

		StepVerifier.create(result).consumeNextWith(e -> {
			assertNotNull(e.getId());
			assertEquals("Rocha Dark Night", e.getName());
		}).verifyComplete();

	}

}
