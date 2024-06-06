package com.woopaca.laboratory.transaction.deadlock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class VersionPost {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private int commentsCount;

    private LocalDateTime createdAt;

    @Version
    private long version;

    public VersionPost() {
    }

    @Builder
    public VersionPost(String title, String content) {
        this.title = title;
        this.content = content;
        this.commentsCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void increaseCommentsCount() {
        commentsCount++;
    }
}
