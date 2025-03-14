package com.aetna.movies.dto;

import java.util.List;

public class MovieSummary {
    int limit;
    int offset;
    int page;
    List<Movie> movies;
}
