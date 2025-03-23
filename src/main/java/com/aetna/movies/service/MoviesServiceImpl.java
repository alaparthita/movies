package com.aetna.movies.service;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.aetna.movies.dto.Movie;
import com.aetna.movies.dto.Rating;
import com.aetna.movies.entity.MovieEntity;
import com.aetna.movies.exception.MoviesServiceException;
import com.aetna.movies.mapper.EntityMapper;
import com.aetna.movies.repository.MoviesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MoviesServiceImpl implements MoviesService {

    @Value("${ratings.api.endpoint}")
    private String RATINGS_API_ENDPOINT;

    private MoviesRepository moviesRepository;
    private RestClientService restClientService;

    @Autowired
    public MoviesServiceImpl(MoviesRepository moviesRepository, RestClientService restClientService) {
        this.moviesRepository = moviesRepository;
        this.restClientService = restClientService;
    }

    @Override
    public List<Movie> getAllMovies(int page, int size) {
        try {
            log.info("Page {} of {} movies ", page, size);
            Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "title");
            Page<MovieEntity> moviesPage = moviesRepository.findAll(pageable);

            List<Movie> movies = moviesPage.stream()
                    .map(EntityMapper::toDto)
                    .toList();

            if (!movies.isEmpty()) {
                try {
                    List<Rating> ratings = getMovieRatings(moviesPage.stream().mapToInt(MovieEntity::getMovieId).toArray());

                    if (!ratings.isEmpty()) {
                        for (Movie movie : movies) {
                            ratings.stream()
                                    .filter(rating -> rating.getMovieId() == movie.getMovieId())
                                    .findFirst().ifPresent(dto -> movie.setMovieRating(dto.getRating()));
                        }
                    }
                } catch (Exception e) {
                    log.error("Error fetching ratings: {}", e.getMessage());
                    // Set default rating to 0.0 for all movies when rating service fails
                    movies.forEach(movie -> movie.setMovieRating(0.0));
                }
            }

            return movies;

        } catch (Exception e) {
            throw new MoviesServiceException("Exception occurred while fetching movies", e);
        }
    }

    @Override
    public List<Movie> getAllMoviesByYear(int year, int page, int size) {
        try {
            log.info("Requesting movies for year {}", year);
            Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "title");
            Page<MovieEntity> moviesPage = moviesRepository.getMoviesByYear(year, pageable);
            log.info("Movies {} of year {}", moviesPage.getNumberOfElements(), year);
            List<Movie> movies = moviesPage.stream()
                    .map(EntityMapper::toDto)
                    .collect(Collectors.toList());

            return movies;
        } catch (Exception e) {
            throw new MoviesServiceException("Exception occurred while fetching movies", e);
        }
    }

    @Override
    public List<Movie> getAllMoviesByGenre(String genre, int page, int size) {
        try {
            if (genre == null || genre.trim().isEmpty()) {
                throw new IllegalArgumentException("Genre cannot be null or empty");
            }
            
            log.info("Requesting movies for genre {}", genre);
            Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "title");
            Page<MovieEntity> moviesPage = moviesRepository.getMoviesByGenre(genre.trim(), pageable);
            log.info("Found {} movies of genre {}", moviesPage.getNumberOfElements(), genre);
            
            List<Movie> movies = moviesPage.stream()
                    .map(EntityMapper::toDto)
                    .collect(Collectors.toList());

            if (!movies.isEmpty()) {
                try {
                    List<Rating> ratings = getMovieRatings(moviesPage.stream()
                            .mapToInt(MovieEntity::getMovieId)
                            .toArray());

                    if (!ratings.isEmpty()) {
                        for (Movie movie : movies) {
                            ratings.stream()
                                    .filter(rating -> rating.getMovieId() == movie.getMovieId())
                                    .findFirst()
                                    .ifPresent(dto -> movie.setMovieRating(dto.getRating()));
                        }
                    }
                } catch (Exception e) {
                    log.error("Error fetching ratings: {}", e.getMessage());
                    // Set default rating to 0.0 for all movies when rating service fails
                    movies.forEach(movie -> movie.setMovieRating(0.0));
                }
            }

            return movies;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new MoviesServiceException("Exception occurred while fetching movies by genre: " + genre, e);
        }
    }

    @Override
    public Movie getMovieDetails(int movieId) {
        log.info("Fetch details for movie id {} ", movieId);
        try {
            Optional<MovieEntity> movieEntity = moviesRepository.findById(movieId);

            log.info("Found movie {}", movieEntity);

            if (movieEntity.isPresent()) {
                Movie movie = EntityMapper.toDto(movieEntity.get());
                try {
                    int[] movieArr = { movieId };
                    List<Rating> ratings = getMovieRatings(movieArr);
                    if (!ratings.isEmpty()) {
                        movie.setMovieRating(ratings.get(0).getRating());
                    }
                } catch (Exception e) {
                    log.error("Error fetching ratings for movie {}: {}", movieId, e.getMessage());
                    movie.setMovieRating(0.0);
                }
                return movie;
            }

        } catch (Exception e) {
            throw new MoviesServiceException("Exception occurred while fetching movie details", e);
        }

        return null;
    }

    private List<Rating> getMovieRatings(int[] movieIds) {
        Gson gson = new Gson();
        String jsonArray = gson.toJson(movieIds);
        try {
            HttpResponse<String> response = restClientService.post(RATINGS_API_ENDPOINT, jsonArray);

            if (response.statusCode() == 200) {
                log.info("Movies successfully retrieved");
                ObjectMapper objectMapper = new ObjectMapper();
                Rating[] ratingList = objectMapper.readValue(response.body(), Rating[].class);
                return Arrays.asList(ratingList);
            } else {
                log.info("Movies retrieval failed: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new MoviesServiceException("Exception occurred while fetching movie ratings", e);
        }
        return Collections.emptyList();
    }
}
