package com.aetna.movies.service;

import java.util.List;

import com.aetna.movies.dto.Movie;

public interface MoviesService {

    List<Movie> getAllMovies(int page, int size);

    List<Movie> getAllMoviesByYear(int year, int page, int size);

    List<Movie> getAllMoviesByGenre(String genre, int page, int size);

    Movie getMovieDetails(int movieId);
}
