package com.aetna.movies.service;

import java.io.IOException;
import java.net.http.HttpResponse;

public interface RestClientService {

    HttpResponse<String> get(String uri) throws IOException, InterruptedException;
    HttpResponse<String> post(String uri, String body) throws IOException, InterruptedException;
}
