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

    private Long postId;

    public NonRelationshipComment() {
    }

    @Builder
    public NonRelationshipComment(String content, Long postId) {
        this.content = content;
        this.writtenAt = LocalDateTime.now();
        this.postId = postId;
    }
}
