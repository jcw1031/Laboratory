package com.woopaca.laboratory.transaction.deadlock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class NonRelationshipComment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String content;

    private LocalDateTime writtenAt;

    public NonRelationshipComment() {
    }

    @Builder
    public NonRelationshipComment(String content) {
        this.content = content;
        this.writtenAt = LocalDateTime.now();
    }
}
