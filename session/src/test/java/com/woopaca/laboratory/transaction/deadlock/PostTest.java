package com.woopaca.laboratory.transaction.deadlock;

import com.woopaca.laboratory.transaction.deadlock.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

@Slf4j
class PostTest {

    @Test
    void should_success_createPostEntity() {
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();
        LocalDateTime createdAt = post.getCreatedAt();
        int commentsCount = post.getCommentsCount();
        log.info("createdAt = {}", createdAt);
        log.info("commentsCount = {}", commentsCount);
    }
}