package com.aetna.movies.mapper;

import com.aetna.movies.dto.Movie;
import com.aetna.movies.entity.MovieEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    public EntityMapper() {}

    public static Movie toDto(MovieEntity movieEntity) {
        Movie movie = Movie.builder()
                            .movieId(movieEntity.getMovieId())
                            .imdbId(movieEntity.getImdbId())
                            .title(movieEntity.getTitle())
                            .overview(movieEntity.getOverview())
                            .releaseDate(movieEntity.getReleaseDate())
                            .budget("$"+ movieEntity.getBudget())
                            .revenue(movieEntity.getRevenue())
                            .runtime(movieEntity.getRuntime())
                            .language(movieEntity.getLanguage())
                            .genres(parseJson(movieEntity.getGenres()))
                            .build();
        return movie;
    }

    private static List<String> parseJson(String jsonString) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parse JSON string into a list of JsonNode objects
            List<JsonNode> genres = objectMapper.readValue(jsonString, new TypeReference<List<JsonNode>>() {});
            return genres.stream()
                    .map(genre -> genre.get("name").asText())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON: " + jsonString, e);
        }
    }
}
