package com.rocha.aws.app.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.rocha.aws.app.domain.MovieInfo;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String>{

}
