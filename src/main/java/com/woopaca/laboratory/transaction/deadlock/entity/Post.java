package com.woopaca.laboratory.transaction.deadlock.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Post {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private int commentsCount;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<Comment> comments = new ArrayList<>();

    public Post() {
    }

    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
        this.commentsCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void writeComment(Comment comment) {
        this.comments.add(comment);
        commentsCount++;
    }
}
