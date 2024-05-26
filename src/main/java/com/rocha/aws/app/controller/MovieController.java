package com.rocha.aws.app.controller;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rocha.aws.app.domain.MovieInfo;
import com.rocha.aws.app.service.MovieInfoService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MovieController {
	
	private final MovieInfoService movieInfoService;
	
	public MovieController(MovieInfoService movieInfoService) {
		super();
		this.movieInfoService = movieInfoService;
	}

	@GetMapping("/flux")
	public Flux<Integer> flux(){
		return Flux.just(1,2,3).log();
	}
	
	@GetMapping("/mono")
	public Mono<String> mono(){
		return Mono.just("Hello world").log();
	}
	
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Flux<Long> stream(){
		return Flux.interval(Duration.ofMillis(1000)).log();
	}
	
	@PostMapping("/movies")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<MovieInfo> create(@RequestBody @Valid MovieInfo movie){
		return movieInfoService.create(movie);
	}
	
	@GetMapping("/movies")
	@ResponseStatus(HttpStatus.OK)
	public Flux<MovieInfo> findAll(){
		return movieInfoService.findAll();
	}
	
	@GetMapping("/movies/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ResponseEntity<MovieInfo>> findById(@PathVariable String id){
		return movieInfoService.findById(id).map(movie -> {
			return ResponseEntity.ok().body(movie);
		}).switchIfEmpty(Mono.just( ResponseEntity.notFound().build()));
	}
	
	@PutMapping("/movies/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ResponseEntity<MovieInfo>> update(@RequestBody @Valid MovieInfo movie, @PathVariable String id){
		return movieInfoService.update(movie, id).map(updatedMovie->{
			return ResponseEntity.ok().body(updatedMovie);
		}).switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}
	
	@DeleteMapping("/movies/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> deleteById(@PathVariable String id){
		return movieInfoService.deleteById(id);
	}

}
