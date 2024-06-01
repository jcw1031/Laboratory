package com.woopaca.laboratory.transaction.deadlock;

import com.woopaca.laboratory.concurrent.ParallelTest;
import com.woopaca.laboratory.transaction.deadlock.entity.Post;
import com.woopaca.laboratory.transaction.deadlock.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PostServiceTest extends ParallelTest {

    public static final int TOTAL_COUNT = 3;

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    Post post;

    @BeforeEach
    void setUp() {
        post = postRepository.save(
                Post.builder()
                        .title("title")
                        .content("content")
                        .build()
        );
    }

    @Test
    void increaseCommentsCountTest() throws InterruptedException {
        executionParallel(index -> postService.updatePostCommentsCount(post.getId()), TOTAL_COUNT);
    }
}