package com.rocha.aws.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocha.aws.app.domain.MovieInfo;
import com.rocha.aws.app.repository.MovieInfoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {
	
	@Autowired
	private MovieInfoRepository repository;
	
	
	public Mono<MovieInfo> create(MovieInfo movie){
		return repository.save(movie);
	}
	
	public Flux<MovieInfo> findAll(){
		return repository.findAll();
	}
	
	
	public Mono<MovieInfo> findById(String id){
		return repository.findById(id);
	}
	
	public Mono<Void> deleteById(String id){
		return repository.deleteById(id);
	}
	
	public Mono<MovieInfo> update(MovieInfo movie, String id){
		return repository.findById(id).flatMap(moviedb ->{
			moviedb.setCast(movie.getCast());
			moviedb.setName(movie.getName());
			moviedb.setReleaseDate(movie.getReleaseDate());
			moviedb.setYear(movie.getYear());
			return repository.save(movie);
		});
		
	}

}
