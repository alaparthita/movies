package com.aetna.movies.controller;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.aetna.movies.dto.Movie;
import com.aetna.movies.exception.GlobalExceptionHandler;
import com.aetna.movies.exception.MoviesServiceException;
import com.aetna.movies.exception.ResourceNotFoundException;
import com.aetna.movies.service.MoviesService;

class MoviesControllerTest {

    @Mock
    private MoviesService moviesService;

    @InjectMocks
    private MoviesController moviesController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(moviesController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Replace with an existing exception handler
                .build();
    }

    @Test
    void testGetAllMovies_Success() throws Exception {
        // Arrange
        Movie movie = new Movie();
        movie.setMovieId(1);
        movie.setTitle("Test Movie");
        when(moviesService.getAllMovies(1, 50)).thenReturn(List.of(movie));

        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/")
                        .param("page", "1")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Movie"));

        verify(moviesService, times(1)).getAllMovies(1, 50);
    }

    @Test
    void testGetAllMovies_NotFound() throws Exception {
        // Arrange
        when(moviesService.getAllMovies(1, 50)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/")
                        .param("page", "1")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(moviesService, times(1)).getAllMovies(1, 50);
    }

    @Test
    void testGetMovieDetails_Success() throws Exception {
        // Arrange
        Movie movie = new Movie();
        movie.setMovieId(1);
        movie.setTitle("Test Movie");
        when(moviesService.getMovieDetails(1)).thenReturn(movie);

        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Movie"));

        verify(moviesService, times(1)).getMovieDetails(1);
    }

    @Test
    void testGetMovieDetails_NotFound() throws Exception {
        // Arrange
        when(moviesService.getMovieDetails(1)).thenThrow(new ResourceNotFoundException("Movie not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(moviesService, times(1)).getMovieDetails(1);
    }

    @Test
    void testGetMoviesByYear_Success() throws Exception {
        // Arrange
        Movie movie = new Movie();
        movie.setMovieId(1);
        movie.setTitle("Test Movie");
        when(moviesService.getAllMoviesByYear(2022, 1, 50)).thenReturn(List.of(movie));

        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/year/2022")
                        .param("page", "1")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Movie"));

        verify(moviesService, times(1)).getAllMoviesByYear(2022, 1, 50);
    }

    @Test
    void testGetMoviesByGenre_Success() throws Exception {
        // Arrange
        Movie movie = new Movie();
        movie.setMovieId(1);
        movie.setTitle("Test Movie");
        when(moviesService.getAllMoviesByGenre("Action", 1, 50)).thenReturn(List.of(movie));

        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/genre/Action")
                        .param("page", "1")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Movie"));

        verify(moviesService, times(1)).getAllMoviesByGenre("Action", 1, 50);
    }

    @Test
    void testGetMoviesByGenre_NotFound() throws Exception {
        // Arrange
        when(moviesService.getAllMoviesByGenre("Action", 1, 50)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/genre/Action")
                        .param("page", "1")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(moviesService, times(1)).getAllMoviesByGenre("Action", 1, 50);
    }

    @Test
    void testGetAllMovies_ServiceException() throws Exception {
        // Arrange
        when(moviesService.getAllMovies(1, 50)).thenThrow(new MoviesServiceException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/")
                        .param("page", "1")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Database error"));

        verify(moviesService, times(1)).getAllMovies(1, 50);
    }

    @Test
    void testGetAllMovies_InvalidPagination() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/")
                        .param("page", "-1")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMoviesByYear_NotFound() throws Exception {
        // Arrange
        when(moviesService.getAllMoviesByYear(2022, 1, 50)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/year/2022")
                        .param("page", "1")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(moviesService, times(1)).getAllMoviesByYear(2022, 1, 50);
    }

    @Test
    void testGetMoviesByYear_ServiceException() throws Exception {
        // Arrange
        when(moviesService.getAllMoviesByYear(2022, 1, 50)).thenThrow(new MoviesServiceException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/year/2022")
                        .param("page", "1")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Database error"));

        verify(moviesService, times(1)).getAllMoviesByYear(2022, 1, 50);
    }

    @Test
    void testGetMoviesByYear_InvalidYearFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/year/invalid")
                        .param("page", "1")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to convert value of type 'java.lang.String' to required type 'int'"));
    }

    @Test
    void testGetMoviesByYear_InvalidPagination() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/year/2022")
                        .param("page", "-1")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMoviesByGenre_ServiceException() throws Exception {
        // Arrange
        when(moviesService.getAllMoviesByGenre("Action", 1, 50)).thenThrow(new MoviesServiceException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/genre/Action")
                        .param("page", "1")
                        .param("size", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Database error"));

        verify(moviesService, times(1)).getAllMoviesByGenre("Action", 1, 50);
    }

    @Test
    void testGetMoviesByGenre_InvalidPagination() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/movies/genre/Action")
                        .param("page", "-1")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}