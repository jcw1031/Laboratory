package com.woopaca.laboratory.transaction.deadlock.service;

import com.woopaca.laboratory.transaction.deadlock.aop.Retry;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final VersionPostRepository versionPostRepository;
    private final VersionCommentRepository versionCommentRepository;
    private final NonRelationshipPostRepository nonRelationshipPostRepository;
    private final NonRelationshipCommentRepository nonRelationshipCommentRepository;
    private final EntityManager entityManager;

    public CommentService(PostRepository postRepository, CommentRepository commentRepository, VersionPostRepository versionPostRepository, VersionCommentRepository versionCommentRepository, NonRelationshipPostRepository nonRelationshipPostRepository, NonRelationshipCommentRepository nonRelationshipCommentRepository, EntityManager entityManager) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.versionPostRepository = versionPostRepository;
        this.versionCommentRepository = versionCommentRepository;
        this.nonRelationshipPostRepository = nonRelationshipPostRepository;
        this.nonRelationshipCommentRepository = nonRelationshipCommentRepository;
        this.entityManager = entityManager;
    }

    public void writeCommentWithTransactionDeadlock(Long postId, String commentContent) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("post not found for post id [%d]", postId)));
        Comment comment = new Comment(commentContent, post);
        post.increaseCommentsCount();
        commentRepository.save(comment);
    }

    public void writeCommentAfterIncreaseCommentCount(Long postId, String commentContent) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("post not found for post id [%d]", postId)));
        post.increaseCommentsCount();

        Comment comment = new Comment(commentContent, post);
        commentRepository.save(comment);
    }

    public void writeCommentNonRelationship(Long postId, String commentContent) {
        NonRelationshipPost post = nonRelationshipPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("post not found for post id [%d]", postId)));
        NonRelationshipComment comment = new NonRelationshipComment(commentContent, postId);
        post.increaseCommentCount();
        nonRelationshipCommentRepository.save(comment);
    }

    public synchronized void writeCommentWithSynchronized(Long postId, String commentContent) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("post not found for post id [%d]", postId)));
        Comment comment = new Comment(commentContent, post);
        post.writeComment(comment);
        commentRepository.save(comment);
    }

    @Retry
    public void writeCommentWithOptimisticLock(Long postId, String commentContent) {
        VersionPost post = versionPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("post not found for post id [%d]", postId)));
        VersionComment comment = new VersionComment(commentContent, post.getId());
        post.increaseCommentsCount();
        versionCommentRepository.save(comment);
    }

    public void writeCommentWithPessimisticLock(Long postId, String commentContent) {
        Post post = postRepository.findByIdForUpdate(postId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("post not found for post id [%d]", postId)));
        Comment comment = new Comment(commentContent, post);
        post.writeComment(comment);
        commentRepository.save(comment);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void writeCommentWithoutTransaction(Long postId, String commentContent) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("post not found for post id [%d]", postId)));
        Comment comment = new Comment(commentContent, post);
        post.increaseCommentsCount();
        commentRepository.save(comment);
        postRepository.save(post);
    }

    public void writeCommentWithEntityManager() {
        Post post = entityManager.find(Post.class, 1, LockModeType.PESSIMISTIC_WRITE);
    }

}
