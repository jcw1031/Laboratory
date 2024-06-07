package com.woopaca.laboratory.transaction.deadlock;

import com.woopaca.laboratory.transaction.concurrent.ParallelTest;
import com.woopaca.laboratory.transaction.deadlock.entity.Comment;
import com.woopaca.laboratory.transaction.deadlock.entity.NonRelationshipComment;
import com.woopaca.laboratory.transaction.deadlock.entity.NonRelationshipPost;
import com.woopaca.laboratory.transaction.deadlock.entity.Post;
import com.woopaca.laboratory.transaction.deadlock.entity.VersionComment;
import com.woopaca.laboratory.transaction.deadlock.entity.VersionPost;
import com.woopaca.laboratory.transaction.deadlock.repository.CommentRepository;
import com.woopaca.laboratory.transaction.deadlock.repository.NonRelationshipCommentRepository;
import com.woopaca.laboratory.transaction.deadlock.repository.NonRelationshipPostRepository;
import com.woopaca.laboratory.transaction.deadlock.repository.PostRepository;
import com.woopaca.laboratory.transaction.deadlock.repository.VersionCommentRepository;
import com.woopaca.laboratory.transaction.deadlock.repository.VersionPostRepository;
import com.woopaca.laboratory.transaction.deadlock.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@Slf4j
@DisplayName("트랜잭션 교착 상태 테스트")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CommentServiceTest extends ParallelTest {

    static final int TOTAL_COUNT = 3;

    @Autowired
    CommentService commentService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VersionPostRepository versionPostRepository;

    @Autowired
    NonRelationshipPostRepository nonRelationshipPostRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    NonRelationshipCommentRepository nonRelationshipCommentRepository;

    @Autowired
    VersionCommentRepository versionCommentRepository;

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

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @DisplayName("교착 상태 발생 테스트")
    @Test
    void should_throwException_forTransactionDeadlock() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentWithTransactionDeadlock(this.post.getId(), commentContent);
        }, TOTAL_COUNT);

        List<Comment> allComments = commentRepository.findAll();
        Post post = postRepository.findById(this.post.getId())
                .orElseThrow();

        assertThat(allComments).size().isNotEqualTo(TOTAL_COUNT);
        assertThat(post.getCommentsCount()).isNotEqualTo(TOTAL_COUNT);
    }

    @DisplayName("댓글 개수 증가 후 댓글 저장 테스트")
    @Test
    void increaseCommentCount_before_insertComment() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentAfterIncreaseCommentCount(post.getId(), commentContent);
        }, TOTAL_COUNT);

        List<Comment> allComments = commentRepository.findAll();
        Post post = postRepository.findById(this.post.getId())
                .orElseThrow();

        assertThat(allComments).size().isEqualTo(TOTAL_COUNT);
        assertThat(post.getCommentsCount()).isNotEqualTo(TOTAL_COUNT);
    }

    @DisplayName("외래키 제약 조건 제거 테스트")
    @Test
    void nonRelationshipTest() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentNonRelationship(post.getId(), commentContent);
        }, TOTAL_COUNT);

        List<NonRelationshipComment> allComments = nonRelationshipCommentRepository.findAll();
        NonRelationshipPost nonRelationshipPost = nonRelationshipPostRepository.findById(this.nonRelationshipPost.getId())
                .orElseThrow();

        assertThat(allComments).size().isEqualTo(TOTAL_COUNT);
        assertThat(nonRelationshipPost.getCommentsCount()).isNotEqualTo(TOTAL_COUNT);
    }

    @DisplayName("synchronized 키워드 테스트")
    @Test
    void synchronizedTest() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentWithSynchronized(post.getId(), commentContent);
        }, TOTAL_COUNT);

        List<Comment> allComments = commentRepository.findAll();
        Post post = postRepository.findById(this.post.getId())
                .orElseThrow();

        assertThat(allComments).size().isEqualTo(TOTAL_COUNT);
        assertThat(post.getCommentsCount()).isNotEqualTo(TOTAL_COUNT);
    }

    @DisplayName("낙관적 락 테스트")
    @Test
    void optimisticLockTest() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentWithOptimisticLock(versionPost.getId(), commentContent);
        }, TOTAL_COUNT);

        List<VersionComment> allComments = versionCommentRepository.findAll();
        VersionPost versionPost = versionPostRepository.findById(this.versionPost.getId())
                .orElseThrow();

        assertThat(allComments).size().isEqualTo(TOTAL_COUNT);
        assertThat(versionPost.getCommentsCount()).isEqualTo(TOTAL_COUNT);
    }

    @DisplayName("비관적 락 테스트")
    @Test
    void pessimisticLockTest() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentWithPessimisticLock(post.getId(), commentContent);
        }, TOTAL_COUNT);

        List<Comment> allComments = commentRepository.findAll();
        Post post = postRepository.findById(this.post.getId())
                .orElseThrow();

        assertThat(allComments).size().isEqualTo(TOTAL_COUNT);
        assertThat(post.getCommentsCount()).isEqualTo(TOTAL_COUNT);
    }

    @DisplayName("트랜잭션 제거 테스트")
    @Test
    void withoutTransactionTest() throws InterruptedException {
        executionParallel(index -> {
            String commentContent = String.format("comment%d", index);
            commentService.writeCommentWithoutTransaction(post.getId(), commentContent);
        }, TOTAL_COUNT);

        List<Comment> allComments = commentRepository.findAll();
        Post post = postRepository.findById(this.post.getId())
                .orElseThrow();

        assertThat(allComments).size().isEqualTo(TOTAL_COUNT);
        assertThat(post.getCommentsCount()).isNotEqualTo(TOTAL_COUNT);
    }

}
