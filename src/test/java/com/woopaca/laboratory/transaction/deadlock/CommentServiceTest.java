package com.woopaca.laboratory.transaction.deadlock;

import com.woopaca.laboratory.concurrent.ParallelTest;
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
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CommentServiceTest extends ParallelTest {

    @Autowired
    CommentService commentService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VersionPostRepository versionPostRepository;

    @Autowired
    private NonRelationshipPostRepository nonRelationshipPostRepository;

    static final int TOTAL_COUNT = 3;

    Post post;
    VersionPost versionPost;
    NonRelationshipPost nonRelationshipPost;

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
    }

    @Test
    void should_throwException_forTransactionDeadlock() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentWithTransactionDeadlock(post.getId(), commentContent);
        }, TOTAL_COUNT);
    }

    @Test
    void increaseCommentCount_before_insertComment() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentAfterIncreaseCommentCount(post.getId(), commentContent);
        }, TOTAL_COUNT);
    }

    @Test
    void nonRelationshipTest() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentNonRelationship(post.getId(), commentContent);
        }, TOTAL_COUNT);
    }

    @Test
    void synchronizedTest() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentWithSynchronized(post.getId(), commentContent);
        }, TOTAL_COUNT);
    }

    @Test
    void optimisticLockTest() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentWithOptimisticLock(post.getId(), commentContent);
        }, TOTAL_COUNT);
    }

    @Test
    void pessimisticLockTest() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentWithPessimisticLock(post.getId(), commentContent);
        }, TOTAL_COUNT);
    }

}
