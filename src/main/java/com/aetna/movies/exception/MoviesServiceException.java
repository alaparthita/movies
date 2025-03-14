package com.aetna.movies.exception;

public class MoviesServiceException extends RuntimeException{
    public MoviesServiceException(String message, Throwable cause) {
        super(message, cause);
        cause.printStackTrace();
    }
    public MoviesServiceException(String message) {
        super(message);
    }
}
