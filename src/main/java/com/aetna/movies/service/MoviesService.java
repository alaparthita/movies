package com.aetna.movies.service;

import com.aetna.movies.dto.Movie;

import java.util.List;

public interface MoviesService {

    List<Movie> getAllMovies(int page, int size);

    List<Movie> getAllMoviesByYear(int year, int page, int size);

    List<Movie> getAllMoviesByGenre(String genre, int page, int size);

    Movie getMovieDetails(int movieId);
}
