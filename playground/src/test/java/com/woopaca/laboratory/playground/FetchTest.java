package com.woopaca.laboratory.playground;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class FetchTest {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Test
    void test() {
        URI uri = URI.create("https://www.kongju.ac.kr/bbs/KNU/2132/414904/artclView.do");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        int count = 10_000;
        for (int i = 0; i < count; i++) {
            try {
                Thread.sleep(300);
                httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            } catch (IOException | InterruptedException e) {
                log.warn(e.getMessage());
            }
        }
    }
}
