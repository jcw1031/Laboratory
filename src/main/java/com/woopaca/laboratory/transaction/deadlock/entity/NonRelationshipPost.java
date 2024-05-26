package com.woopaca.laboratory.transaction.deadlock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class NonRelationshipPost {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private int commentsCount;

    private LocalDateTime createdAt;

    public NonRelationshipPost() {
    }

    @Builder
    public NonRelationshipPost(String title, String content) {
        this.title = title;
        this.content = content;
        this.commentsCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void increaseCommentCount() {
        commentsCount++;
    }
}
