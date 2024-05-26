package com.woopaca.laboratory.transaction.deadlock;

import com.woopaca.laboratory.transaction.deadlock.entity.NonRelationshipPost;
import com.woopaca.laboratory.transaction.deadlock.entity.Post;
import com.woopaca.laboratory.transaction.deadlock.entity.VersionPost;
import com.woopaca.laboratory.transaction.deadlock.repository.NonRelationshipPostRepository;
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
import java.util.function.BiConsumer;

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
    NonRelationshipPost nonRelationshipPost;

    ExecutorService executorService;
    CountDownLatch latch;
    @Autowired
    private NonRelationshipPostRepository nonRelationshipPostRepository;

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

        nonRelationshipPost = nonRelationshipPostRepository.save(
                NonRelationshipPost.builder()
                        .title("title")
                        .content("content")
                        .build()
        );

        executorService = Executors.newFixedThreadPool(TOTAL_COUNT);
        latch = new CountDownLatch(TOTAL_COUNT);
    }

    @Test
    void should_throwException_forTransactionDeadlock() throws InterruptedException {
        executionParallel((postId, commentContent) ->
                commentService.writeCommentWithTransactionDeadlock(postId, commentContent));
    }

    @Test
    void increaseCommentCount_before_insertComment() throws InterruptedException {
        executionParallel((postId, commentContent) ->
                commentService.writeCommentAfterIncreaseCommentCount(postId, commentContent));
    }

    @Test
    void nonRelationshipTest() throws InterruptedException {
        executionParallel((postId, commentContent) ->
                commentService.writeCommentNonRelationship(postId, commentContent));
    }

    @Test
    void synchronizedTest() throws InterruptedException {
        executionParallel((postId, commentContent) ->
                commentService.writeCommentWithSynchronized(postId, commentContent));
    }

    @Test
    void optimisticLockTest() throws InterruptedException {
        executionParallel((postId, commentContent) ->
                commentService.writeCommentWithOptimisticLock(postId, commentContent));

    }

    @Test
    void pessimisticLockTest() throws InterruptedException {
        executionParallel((postId, commentContent) ->
                commentService.writeCommentWithPessimisticLock(postId, commentContent));
    }

    private void executionParallel(BiConsumer<Long, String> consumer) throws InterruptedException {
        for (int i = 0; i < TOTAL_COUNT; i++) {
            int commentNumber = i + 1;
            executorService.submit(() -> {
                try {
                    consumer.accept(post.getId(), String.format("comment%d", commentNumber));
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