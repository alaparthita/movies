package com.aetna.movies.repository;

import com.aetna.movies.entity.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviesRepository extends JpaRepository<MovieEntity, Integer>, PagingAndSortingRepository<MovieEntity, Integer> {

    @Query("SELECT m FROM MovieEntity m WHERE CAST(SUBSTR(m.releaseDate, 1, 4) AS INTEGER) = :year")
    Page<MovieEntity> getMoviesByYear(@Param("year") int year, Pageable pageable);

    // The below query worked in sqlite console but I couldn't get it to work here. In the interest of time, went with simple solution
    // @Query(value = "SELECT *, (json_extract(value, '$.name')) AS genre_names FROM movies m, json_each(m.genres) WHERE genre_names = :genre  group by movieId", nativeQuery = true)
    @Query("SELECT m FROM MovieEntity m WHERE m.genres like :genre")
    Page<MovieEntity> getMoviesByGenre(@Param("genre") String genre, Pageable pageable);
}
