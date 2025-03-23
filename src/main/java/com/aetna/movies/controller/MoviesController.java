package com.aetna.movies.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aetna.movies.dto.Movie;
import com.aetna.movies.exception.ResourceNotFoundException;
import com.aetna.movies.service.MoviesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/movies")
@Slf4j
@Tag(name = "Movies", description = "API for managing movies")
public class MoviesController {

    @Autowired
    MoviesService moviesService;

    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    private void validatePaginationParams(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters: page must be >= 0 and size must be > 0");
        }
    }

    @Operation(summary = "Get all movies", description = "Retrieve a paginated list of all movies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved movies",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class))),
            @ApiResponse(responseCode = "404", description = "No movies found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Movie>> getAllMovies(
            @Parameter(description = "Page number", example = "1") @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "50") @RequestParam(value = "size", defaultValue = "50") int size) {

        validatePaginationParams(page, size);
        List<Movie> movies = moviesService.getAllMovies(page, size);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found");
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @Operation(summary = "Get movie details", description = "Retrieve details of a specific movie by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved movie details",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @RequestMapping(value = "/{movieId}", method = RequestMethod.GET)
    public ResponseEntity<?> getMovieDetails(
            @Parameter(description = "ID of the movie", example = "123") @PathVariable("movieId") int movieId) {

        Movie movie = moviesService.getMovieDetails(movieId);
        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found");
        }
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @Operation(summary = "Get movies by year", description = "Retrieve a paginated list of movies released in a specific year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved movies",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class))),
            @ApiResponse(responseCode = "404", description = "No movies found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @RequestMapping(value = "/year/{year}", method = RequestMethod.GET)
    public ResponseEntity<List<Movie>> getMoviesByYear(
            @Parameter(description = "Year of release", example = "2022") @PathVariable("year") int year,
            @Parameter(description = "Page number", example = "1") @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "50") @RequestParam(value = "size", defaultValue = "50") int size) {

        validatePaginationParams(page, size);
        List<Movie> movies = moviesService.getAllMoviesByYear(year, page, size);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found");
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @Operation(summary = "Get movies by genre", description = "Retrieve a paginated list of movies by genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved movies",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Movie.class))),
            @ApiResponse(responseCode = "404", description = "No movies found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @RequestMapping(value = "/genre/{genre}", method = RequestMethod.GET)
    public ResponseEntity<List<Movie>> getAllMoviesByGenre(
            @Parameter(description = "Genre of the movies", example = "Action") @PathVariable("genre") String genre,
            @Parameter(description = "Page number", example = "1") @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "50") @RequestParam(value = "size", defaultValue = "50") int size) {

        validatePaginationParams(page, size);
        List<Movie> movies = moviesService.getAllMoviesByGenre(genre, page, size);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found");
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}