package com.woopaca.laboratory.lms.dto;

import java.time.LocalDateTime;

public record Todo(Long courseId, Assignment assignment) {

    record Assignment(String name, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime dueAt) {
    }
}
