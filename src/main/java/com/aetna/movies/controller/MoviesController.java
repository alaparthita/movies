package com.aetna.movies.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aetna.movies.dto.Movie;
import com.aetna.movies.exception.ErrorDetails;
import com.aetna.movies.exception.ResourceNotFoundException;
import com.aetna.movies.service.MoviesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
    private MoviesService moviesService;

    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    private void validatePaginationParams(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters: page must be >= 0 and size must be > 0");
        }
    }

    @Operation(
        summary = "Get all movies",
        description = "Retrieves a paginated list of all movies.",
        operationId = "getAllMovies"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved movies",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Movie.class)),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "[{\"id\": 1, \"title\": \"The Shawshank Redemption\", \"year\": 1994, \"rating\": 9.3}]"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid pagination parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorDetails.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"statusCode\": 400, \"message\": \"Page number must be greater than 0\", \"details\": \"page: 0\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorDetails.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"statusCode\": 500, \"message\": \"An error occurred while retrieving movies\", \"details\": \"Database error\"}"
                )
            )
        )
    })
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Movie>> getAllMovies(
            @Parameter(description = "Page number (1-based)", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page", example = "50") @RequestParam(defaultValue = "50") int size) {
        validatePaginationParams(page, size);
        List<Movie> movies = moviesService.getAllMovies(page, size);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found");
        }
        return ResponseEntity.ok(movies);
    }

    @Operation(
        summary = "Get movie by ID",
        description = "Retrieves a specific movie by its ID. Returns 404 if the movie is not found.",
        operationId = "getMovieById"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved movie",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Movie.class),
                examples = @ExampleObject(
                    value = "{\"movieId\": 1, \"title\": \"The Shawshank Redemption\", \"releaseDate\": \"1994-01-01\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Movie not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorDetails.class),
                examples = @ExampleObject(
                    value = "{\"statusCode\": 404, \"message\": \"Movie not found with id: 1\", \"details\": \"uri=/api/v1/movies/1\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid ID format",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorDetails.class),
                examples = @ExampleObject(
                    value = "{\"statusCode\": 400, \"message\": \"Invalid ID format\", \"details\": \"ID must be a valid integer\"}"
                )
            )
        )
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Movie> getMovieById(
            @Parameter(description = "ID of the movie to retrieve", required = true, example = "1") 
            @PathVariable("id") String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            if (id <= 0) {
                throw new IllegalArgumentException("ID must be a positive integer");
            }
            Movie movie = moviesService.getMovieDetails(id);
            if (movie == null) {
                throw new ResourceNotFoundException("Movie not found with id: " + id);
            }
            return ResponseEntity.ok(movie);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format: " + idStr);
        }
    }

    @Operation(
        summary = "Get movies by year",
        description = "Retrieves a list of movies released in the specified year with pagination support"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved movies",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Movie.class),
                examples = {
                    @ExampleObject(
                        value = "[{\"movieId\": 1, \"title\": \"Movie Title\", \"releaseDate\": \"2022-01-01\"}]"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No movies found for the specified year",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorDetails.class),
                examples = {
                    @ExampleObject(
                        value = "{\"statusCode\": 404, \"message\": \"No movies found for year: 2022\", \"details\": \"uri=/api/v1/movies/year/2022\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid year format",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorDetails.class),
                examples = {
                    @ExampleObject(
                        value = "{\"statusCode\": 400, \"message\": \"Invalid year format\", \"details\": \"Year must be a valid integer\"}"
                    )
                }
            )
        )
    })
    @RequestMapping(value = "/year/{year}", method = RequestMethod.GET)
    public ResponseEntity<List<Movie>> getMoviesByYear(
        @Parameter(description = "Release year of the movies", required = true, example = "2022")
        @PathVariable("year") String yearStr,
        @Parameter(description = "Page number (0-based)", required = false, example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Number of items per page", required = false, example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        try {
            validatePaginationParams(page, size);
            int year = Integer.parseInt(yearStr);
            if (year < 1900 || year > 2100) {
                throw new IllegalArgumentException("Year must be between 1900 and 2100");
            }
            List<Movie> movies = moviesService.getAllMoviesByYear(year, page, size);
            if (movies.isEmpty()) {
                throw new ResourceNotFoundException("No movies found for year: " + year);
            }
            return ResponseEntity.ok(movies);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to convert value of type 'java.lang.String' to required type 'int'");
        }
    }

    @Operation(
        summary = "Get movies by genre",
        description = "Retrieves a list of movies that match the specified genre. Returns 404 if no movies are found.",
        operationId = "getMoviesByGenre"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved movies",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Movie.class)),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "[{\"id\": 1, \"title\": \"The Shawshank Redemption\", \"year\": 1994, \"rating\": 9.3, \"genres\": [\"Drama\"]}]"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid genre parameter",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorDetails.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"statusCode\": 400, \"message\": \"Genre parameter cannot be null or empty\", \"details\": \"genre: null\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No movies found for genre",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorDetails.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"statusCode\": 404, \"message\": \"No movies found for genre: Action\", \"details\": \"genre: Action\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorDetails.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"statusCode\": 500, \"message\": \"An error occurred while retrieving movies by genre\", \"details\": \"Database error\"}"
                )
            )
        )
    })
    @RequestMapping(value = "/genre/{genre}", method = RequestMethod.GET)
    public ResponseEntity<List<Movie>> getMoviesByGenre(
            @Parameter(description = "Genre to filter by", example = "Action") @PathVariable String genre,
            @Parameter(description = "Page number", example = "1") @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "Page size", example = "50") @RequestParam(value = "size", defaultValue = "50") int size) {
        validatePaginationParams(page, size);
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre parameter cannot be null or empty");
        }
        List<Movie> movies = moviesService.getAllMoviesByGenre(genre, page, size);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found for genre: " + genre);
        }
        return ResponseEntity.ok(movies);
    }
}