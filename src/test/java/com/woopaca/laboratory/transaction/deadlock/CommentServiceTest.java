package com.woopaca.laboratory.transaction.deadlock;

import com.woopaca.laboratory.transaction.deadlock.entity.Post;
import com.woopaca.laboratory.transaction.deadlock.entity.VersionPost;
import com.woopaca.laboratory.transaction.deadlock.repository.PostRepository;
import com.woopaca.laboratory.transaction.deadlock.repository.VersionPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VersionPostRepository versionPostRepository;

    static final int TOTAL_COUNT = 3;

    Post post;
    VersionPost versionPost;
    ExecutorService executorService;
    CountDownLatch latch;

    @BeforeEach
    void setUp() {
        post = postRepository.save(
                Post.builder()
                        .title("title")
                        .content("content")
                        .build()
        );

        versionPost = versionPostRepository.save(
                VersionPost.builder()
                        .title("title")
                        .content("content")
                        .build()
        );

        executorService = Executors.newFixedThreadPool(TOTAL_COUNT);
        latch = new CountDownLatch(TOTAL_COUNT);
    }

    @Test
    void should_throwException_forTransactionDeadlock() throws InterruptedException {
        for (int i = 0; i < TOTAL_COUNT; i++) {
            int commentNumber = i + 1;
            executorService.submit(() -> {
                try {
                    commentService.writeCommentWithTransactionDeadlock(post.getId(), String.format("comment%d", commentNumber));
                } catch (Exception e) {
                    log.error("Exception", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }

    @Test
    void optimisticLockTest() throws InterruptedException {
        for (int i = 0; i < TOTAL_COUNT; i++) {
            int commentNumber = i + 1;
            executorService.submit(() -> {
                try {
                    commentService.writeCommentWithOptimisticLock(versionPost.getId(), String.format("comment%d", commentNumber));
                } catch (Exception e) {
                    log.error("Exception", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }

    @Test
    void pessimisticLockTest() throws InterruptedException {
        for (int i = 0; i < TOTAL_COUNT; i++) {
            int commentNumber = i + 1;
            executorService.submit(() -> {
                try {
                    commentService.writeCommentWithPessimisticLock(post.getId(), String.format("comment%d", commentNumber));
                } catch (Exception e) {
                    log.error("Exception", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }
}