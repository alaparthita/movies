package com.aetna.movies.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class Rating {
        int movieId;
        double rating;
}
