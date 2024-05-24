package com.woopaca.laboratory.transaction.deadlock;

import com.woopaca.laboratory.transaction.deadlock.aop.Retry;
import com.woopaca.laboratory.transaction.deadlock.entity.Comment;
import com.woopaca.laboratory.transaction.deadlock.entity.Post;
import com.woopaca.laboratory.transaction.deadlock.entity.VersionComment;
import com.woopaca.laboratory.transaction.deadlock.entity.VersionPost;
import com.woopaca.laboratory.transaction.deadlock.repository.CommentRepository;
import com.woopaca.laboratory.transaction.deadlock.repository.PostRepository;
import com.woopaca.laboratory.transaction.deadlock.repository.VersionCommentRepository;
import com.woopaca.laboratory.transaction.deadlock.repository.VersionPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final VersionPostRepository versionPostRepository;
    private final VersionCommentRepository versionCommentRepository;

    public CommentService(PostRepository postRepository, CommentRepository commentRepository, VersionPostRepository versionPostRepository, VersionCommentRepository versionCommentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.versionPostRepository = versionPostRepository;
        this.versionCommentRepository = versionCommentRepository;
    }

    public void writeCommentWithTransactionDeadlock(Long postId, String commentContent) {
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
        VersionComment comment = new VersionComment(commentContent, post);
        post.writeComment(comment);
        versionCommentRepository.save(comment);
    }

    public void writeCommentWithPessimisticLock(Long postId, String commentContent) {
        Post post = postRepository.findByIdForUpdate(postId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("post not found for post id [%d]", postId)));
        Comment comment = new Comment(commentContent, post);
        post.writeComment(comment);
        commentRepository.save(comment);
    }
}
