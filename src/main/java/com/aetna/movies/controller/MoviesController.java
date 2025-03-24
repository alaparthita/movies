package com.aetna.movies.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aetna.movies.config.ClientRefIdHolder;
import com.aetna.movies.dto.Movie;
import com.aetna.movies.exception.ResourceNotFoundException;
import com.aetna.movies.service.MoviesService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/movies")
@Slf4j
@Tag(name = "Movies", description = "API for managing and retrieving movie information")
public class MoviesController {

    @Autowired
    private MoviesService moviesService;

    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    private void validatePaginationParams(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters: page must be >= 0 and size must be > 0");
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Movie>> getAllMovies(
            @Parameter(description = "Page number (1-based)", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page", example = "50") @RequestParam(defaultValue = "50") int size) {
        validatePaginationParams(page, size);
        log.debug("Getting all movies with clientRefId: {}", ClientRefIdHolder.getClientRefId());
        List<Movie> movies = moviesService.getAllMovies(page, size);
        log.debug("Retrieved {} movies with clientRefId: {}", movies.size(), ClientRefIdHolder.getClientRefId());
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found");
        }
        return ResponseEntity.ok(movies);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Movie> getMovieById(
            @Parameter(description = "ID of the movie to retrieve", required = true, example = "1") 
            @PathVariable("id") String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            if (id <= 0) {
                throw new IllegalArgumentException("ID must be a positive integer");
            }
            log.debug("Getting movie with ID {} and clientRefId: {}", id, ClientRefIdHolder.getClientRefId());
            Movie movie = moviesService.getMovieDetails(id);
            if (movie == null) {
                throw new ResourceNotFoundException("Movie not found with id: " + id);
            }
            log.debug("Retrieved movie {} with clientRefId: {}", movie.getTitle(), ClientRefIdHolder.getClientRefId());
            return ResponseEntity.ok(movie);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format: " + idStr);
        }
    }

    @RequestMapping(value = "/year/{year}", method = RequestMethod.GET)
    public ResponseEntity<List<Movie>> getMoviesByYear(
        @Parameter(description = "Release year of the movies", required = true, example = "2022")
        @PathVariable("year") String yearStr,
        @Parameter(description = "Page number (0-based)", required = false, example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Number of items per page", required = false, example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        validatePaginationParams(page, size);
        try {
            int year = Integer.parseInt(yearStr);
            if (year < 1900 || year > 2100) {
                throw new IllegalArgumentException("Year must be between 1900 and 2100");
            }
            log.debug("Getting movies for year {} with clientRefId: {}", year, ClientRefIdHolder.getClientRefId());
            List<Movie> movies = moviesService.getAllMoviesByYear(year, page, size);
            log.debug("Retrieved {} movies for year {} with clientRefId: {}", movies.size(), year, ClientRefIdHolder.getClientRefId());
            if (movies.isEmpty()) {
                throw new ResourceNotFoundException("No movies found for year: " + year);
            }
            return ResponseEntity.ok(movies);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid year format: " + yearStr);
        }
    }

    @RequestMapping(value = "/genre/{genre}", method = RequestMethod.GET)
    public ResponseEntity<List<Movie>> getMoviesByGenre(
            @Parameter(description = "Genre to filter by", example = "Action") @PathVariable String genre,
            @Parameter(description = "Page number", example = "1") @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "50") @RequestParam(value = "size", defaultValue = "50") int size) {
        validatePaginationParams(page, size);
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre parameter cannot be null or empty");
        }
        log.debug("Getting movies for genre {} with clientRefId: {}", genre, ClientRefIdHolder.getClientRefId());
        List<Movie> movies = moviesService.getAllMoviesByGenre(genre, page, size);
        log.debug("Retrieved {} movies for genre {} with clientRefId: {}", movies.size(), genre, ClientRefIdHolder.getClientRefId());
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found for genre: " + genre);
        }
        return ResponseEntity.ok(movies);
    }
}