package com.woopaca.laboratory.lms;

import com.woopaca.laboratory.lms.dto.Course;
import com.woopaca.laboratory.lms.dto.Todo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class LMSTodoNotification {

    private final RestTemplate restTemplate;
    private final HttpHeaders authorizationHeaders = new HttpHeaders();

    @Value("${lms.token}")
    private String lmsToken;

    public LMSTodoNotification(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void setUp() {
        authorizationHeaders.set(HttpHeaders.AUTHORIZATION, String.join(" ", "Bearer", lmsToken));
    }

    public List<Course> getCourses() {
        URI uri = UriComponentsBuilder.fromUriString("https://knulms.kongju.ac.kr/api/v1/courses")
                .queryParam("page", 1)
                .queryParam("enrollment_state", "active")
                .queryParam("include", "favorites")
                .build()
                .toUri();
        RequestEntity<Object> requestEntity = new RequestEntity<>(authorizationHeaders, HttpMethod.GET, uri);
        return Objects.requireNonNull(restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<Course>>() {
                }).getBody())
                .stream()
                .filter(Course::isFavorite)
                .toList();
    }

    public List<Todo> getTodo(Course course) {
        Long courseId = course.id();
        URI uri = UriComponentsBuilder.fromUriString("https://knulms.kongju.ac.kr/api/v1/courses/")
                .path(String.valueOf(courseId))
                .path("/todo")
                .build()
                .toUri();
        RequestEntity<Object> requestEntity = new RequestEntity<>(authorizationHeaders, HttpMethod.GET, uri);
        return Objects.requireNonNull(restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<Todo>>() {
        }).getBody());
    }
}
