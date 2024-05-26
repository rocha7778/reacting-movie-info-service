package com.rocha.aws.app.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieInfo {

	@Id
	private String id;
	@NotBlank (message = "movieInfo.name must be present")
	private String name;
	@NotNull (message = "movieInfo.year must be present")
	@Positive (message = "movieInfo.year must be a positive value")
	private Integer year;
	private List<String> cast;
	private LocalDate releaseDate;
	
	public MovieInfo clone() {
		return new MovieInfo(id, name, year, cast, releaseDate);
	}
}
