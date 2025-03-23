package com.aetna.movies.service;

import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.aetna.movies.dto.Movie;
import com.aetna.movies.entity.MovieEntity;
import com.aetna.movies.exception.MoviesServiceException;
import com.aetna.movies.repository.MoviesRepository;

class MoviesServiceImplTest {

    @Mock
    private MoviesRepository moviesRepository;

    @Mock
    private RestClientService restClientService;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private MoviesServiceImpl moviesService;

    private MovieEntity movieEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set the ratings API endpoint
        ReflectionTestUtils.setField(moviesService, "RATINGS_API_ENDPOINT", "http://localhost:8081/api/v1/ratings/movies");
        
        // Setup common movie entity
        movieEntity = new MovieEntity();
        movieEntity.setMovieId(1);
        movieEntity.setTitle("Test Movie");
        movieEntity.setGenres("[{\"name\":\"Action\"}]");
        
        // Setup common HTTP response behavior
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("[{\"movieId\":1,\"rating\":4.5}]");
    }

    @Test
    void testGetAllMovies_Success() throws Exception {
        // Arrange
        Page<MovieEntity> moviePage = new PageImpl<>(Collections.singletonList(movieEntity));
        when(moviesRepository.findAll(any(Pageable.class))).thenReturn(moviePage);
        when(restClientService.post(anyString(), anyString())).thenReturn(httpResponse);

        // Act
        List<Movie> movies = moviesService.getAllMovies(0, 10);

        // Assert
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getTitle());
        assertEquals(4.5, movies.get(0).getMovieRating());
        verify(moviesRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllMovies_EmptyList() {
        // Arrange
        Page<MovieEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(moviesRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        List<Movie> movies = moviesService.getAllMovies(0, 10);

        // Assert
        assertNotNull(movies);
        assertTrue(movies.isEmpty());
    }

    @Test
    void testGetAllMovies_RatingServiceFailure() throws Exception {
        // Arrange
        Page<MovieEntity> moviePage = new PageImpl<>(Collections.singletonList(movieEntity));
        when(moviesRepository.findAll(any(Pageable.class))).thenReturn(moviePage);
        
        when(httpResponse.statusCode()).thenReturn(500);
        when(restClientService.post(anyString(), anyString())).thenReturn(httpResponse);

        // Act
        List<Movie> movies = moviesService.getAllMovies(0, 10);

        // Assert
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals(0.0, movies.get(0).getMovieRating()); // Rating should be 0 when service fails
    }

    @Test
    void testGetAllMovies_RatingServiceException() throws Exception {
        // Arrange
        Page<MovieEntity> moviePage = new PageImpl<>(Collections.singletonList(movieEntity));
        when(moviesRepository.findAll(any(Pageable.class))).thenReturn(moviePage);
        when(restClientService.post(anyString(), anyString())).thenThrow(new RuntimeException("Service unavailable"));

        // Act
        List<Movie> movies = moviesService.getAllMovies(0, 10);

        // Assert
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals(0.0, movies.get(0).getMovieRating()); // Rating should be 0 when service throws exception
    }

    @Test
    void testGetAllMoviesByYear_Success() {
        // Arrange
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
    void testGetAllMoviesByYear_EmptyResult() {
        // Arrange
        Page<MovieEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(moviesRepository.getMoviesByYear(eq(2022), any(Pageable.class))).thenReturn(emptyPage);

        // Act
        List<Movie> movies = moviesService.getAllMoviesByYear(2022, 0, 10);

        // Assert
        assertNotNull(movies);
        assertTrue(movies.isEmpty());
    }

    @Test
    void testGetAllMoviesByYear_RepositoryException() {
        // Arrange
        when(moviesRepository.getMoviesByYear(eq(2022), any(Pageable.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(MoviesServiceException.class, () -> moviesService.getAllMoviesByYear(2022, 0, 10));
    }

    @Test
    void testGetAllMoviesByGenre_Success() {
        // Arrange
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
    void testGetAllMoviesByGenre_EmptyResult() {
        // Arrange
        Page<MovieEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(moviesRepository.getMoviesByGenre(eq("%Action%"), any(Pageable.class))).thenReturn(emptyPage);

        // Act
        List<Movie> movies = moviesService.getAllMoviesByGenre("Action", 0, 10);

        // Assert
        assertNotNull(movies);
        assertTrue(movies.isEmpty());
    }

    @Test
    void testGetAllMoviesByGenre_EmptyGenre() {
        // Arrange
        Page<MovieEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(moviesRepository.getMoviesByGenre(eq("%%"), any(Pageable.class))).thenReturn(emptyPage);

        // Act
        List<Movie> movies = moviesService.getAllMoviesByGenre("", 0, 10);

        // Assert
        assertNotNull(movies);
        assertTrue(movies.isEmpty());
    }

    @Test
    void testGetAllMoviesByGenre_RepositoryException() {
        // Arrange
        when(moviesRepository.getMoviesByGenre(eq("%Action%"), any(Pageable.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(MoviesServiceException.class, () -> moviesService.getAllMoviesByGenre("Action", 0, 10));
    }

    @Test
    void testGetMovieDetails_Success() throws Exception {
        // Arrange
        when(moviesRepository.findById(1)).thenReturn(Optional.of(movieEntity));
        when(restClientService.post(anyString(), anyString())).thenReturn(httpResponse);

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
        Movie movie = moviesService.getMovieDetails(1);
        assertNull(movie);
        verify(moviesRepository, times(1)).findById(1);
    }

    @Test
    void testGetMovieDetails_RatingServiceFailure() throws Exception {
        // Arrange
        when(moviesRepository.findById(1)).thenReturn(Optional.of(movieEntity));
        when(httpResponse.statusCode()).thenReturn(500);
        when(restClientService.post(anyString(), anyString())).thenReturn(httpResponse);

        // Act
        Movie movie = moviesService.getMovieDetails(1);

        // Assert
        assertNotNull(movie);
        assertEquals(1, movie.getMovieId());
        assertEquals(0.0, movie.getMovieRating()); // Rating should be 0 when service fails
    }

    @Test
    void testGetMovieDetails_RatingServiceException() throws Exception {
        // Arrange
        when(moviesRepository.findById(1)).thenReturn(Optional.of(movieEntity));
        when(restClientService.post(anyString(), anyString())).thenThrow(new RuntimeException("Service unavailable"));

        // Act
        Movie movie = moviesService.getMovieDetails(1);

        // Assert
        assertNotNull(movie);
        assertEquals(1, movie.getMovieId());
        assertEquals(0.0, movie.getMovieRating()); // Rating should be 0 when service throws exception
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