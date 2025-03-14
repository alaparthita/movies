package com.aetna.movies.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "movies")
public class MovieEntity {

  @Id
  @Column(name = "movieId", nullable = false)
  int movieId;

  @Column(name = "imdbId", nullable = false)
  String imdbId;

  @Column(name = "title", nullable = false)
  String title;

  @Column(name = "overview")
  String overview;

  @Column(name = "productionCompanies")
  String productionCompanies;

  @Column(name = "releaseDate")
  String releaseDate;

  @Column(name = "budget")
  long budget;

  @Column(name = "revenue")
  double revenue;

  @Column(name = "runtime")
  double runtime;

  @Column(name = "language")
  String language;

  @Column(name = "genres")
  String genres;

  @Column(name = "status")
  String status;
  }