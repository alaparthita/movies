package com.aetna.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    int movieId;
    String imdbId;
    String title;
    String overview;
    String productionCompanies;
    String releaseDate;
    double movieRating;
    String budget;
    double revenue;
    double runtime;
    String language;
    List<String> genres;
}
