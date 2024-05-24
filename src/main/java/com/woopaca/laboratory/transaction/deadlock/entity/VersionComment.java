package com.woopaca.laboratory.transaction.deadlock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class VersionComment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String content;

    private LocalDateTime writtenAt;

    @JoinColumn(name = "post")
    @ManyToOne(fetch = FetchType.LAZY)
    private VersionPost post;

    public VersionComment() {
    }

    @Builder
    public VersionComment(String content, VersionPost post) {
        this.content = content;
        this.writtenAt = LocalDateTime.now();
        this.post = post;
    }
}
