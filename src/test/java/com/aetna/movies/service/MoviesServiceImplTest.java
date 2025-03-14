package com.aetna.movies.service;

import com.aetna.movies.dto.Movie;

import com.aetna.movies.entity.MovieEntity;
import com.aetna.movies.exception.MoviesServiceException;
import com.aetna.movies.repository.MoviesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.net.http.HttpResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoviesServiceImplTest {

    @Mock
    private MoviesRepository moviesRepository;

    @Mock
    private RestClientService restClientService;

    @InjectMocks
    private MoviesServiceImpl moviesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMovies_Success() throws Exception{
        // Arrange
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setMovieId(1);
        movieEntity.setTitle("Test Movie");
        Page<MovieEntity> moviePage = new PageImpl<>(Collections.singletonList(movieEntity));
        when(moviesRepository.findAll(any(Pageable.class))).thenReturn(moviePage);

        Rating rating = new Rating(1, 4.5);
        when(restClientService.post(anyString(), anyString())).thenReturn(mockHttpResponse(200, "[{\"movieId\":1,\"rating\":4.5}]"));

        // Act
        List<Movie> movies = moviesService.getAllMovies(0, 10);

        // Assert
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals(4.5, movies.get(0).getMovieRating());
        verify(moviesRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllMoviesByYear_Success() {
        // Arrange
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setMovieId(1);
        movieEntity.setTitle("Test Movie");
        Page<MovieEntity> moviePage = new PageImpl<>(Collections.singletonList(movieEntity));
        when(moviesRepository.getMoviesByYear(eq(2022), any(Pageable.class))).thenReturn(moviePage);

        // Act
        List<Movie> movies = moviesService.getAllMoviesByYear(2022, 0, 10);

        // Assert
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getTitle());
        verify(moviesRepository, times(1)).getMoviesByYear(eq(2022), any(Pageable.class));
    }

    @Test
    void testGetAllMoviesByGenre_Success() {
        // Arrange
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setMovieId(1);
        movieEntity.setTitle("Test Movie");
        Page<MovieEntity> moviePage = new PageImpl<>(Collections.singletonList(movieEntity));
        when(moviesRepository.getMoviesByGenre(eq("%Action%"), any(Pageable.class))).thenReturn(moviePage);

        // Act
        List<Movie> movies = moviesService.getAllMoviesByGenre("Action", 0, 10);

        // Assert
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getTitle());
        verify(moviesRepository, times(1)).getMoviesByGenre(eq("%Action%"), any(Pageable.class));
    }

    @Test
    void testGetMovieDetails_Success() throws Exception{
        // Arrange
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setMovieId(1);
        movieEntity.setTitle("Test Movie");
        when(moviesRepository.findById(1)).thenReturn(Optional.of(movieEntity));

        Rating rating = new Rating(1, 4.5);
        when(restClientService.post(anyString(), anyString())).thenReturn(mockHttpResponse(200, "[{\"movieId\":1,\"rating\":4.5}]"));

        // Act
        Movie movie = moviesService.getMovieDetails(1);

        // Assert
        assertNotNull(movie);
        assertEquals(1, movie.getMovieId());
        assertEquals("Test Movie", movie.getTitle());
        assertEquals(4.5, movie.getMovieRating());
        verify(moviesRepository, times(1)).findById(1);
    }

    @Test
    void testGetMovieDetails_NotFound() {
        // Arrange
        when(moviesRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MoviesServiceException.class, () -> moviesService.getMovieDetails(1));
        verify(moviesRepository, times(1)).findById(1);
    }

    private HttpResponse<String> mockHttpResponse(int statusCode, String body) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);
        return response;
    }
}

// Updated Rating class with the required constructor
 class Rating {
    private int movieId;
    private double rating;

    // Constructor to initialize movieId and rating
    public Rating(int movieId, double rating) {
        this.movieId = movieId;
        this.rating = rating;
    }

    // Getters and setters (if needed)
}