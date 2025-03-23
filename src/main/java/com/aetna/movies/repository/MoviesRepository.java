package com.aetna.movies.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aetna.movies.entity.MovieEntity;

@Repository
public interface MoviesRepository extends JpaRepository<MovieEntity, Integer> {

    @Query("SELECT m FROM MovieEntity m WHERE CAST(SUBSTR(m.releaseDate, 1, 4) AS INTEGER) = :year")
    Page<MovieEntity> getMoviesByYear(@Param("year") int year, Pageable pageable);

    @Query(value = "SELECT * FROM movies WHERE EXISTS (SELECT 1 FROM json_each(genres) WHERE json_extract(value, '$.name') = :genre)", nativeQuery = true)
    Page<MovieEntity> getMoviesByGenre(@Param("genre") String genre, Pageable pageable);

    // Get all movies sorted by release date in descending order
    // List<MovieEntity> findAllByOrderByReleaseDateDesc();
}
