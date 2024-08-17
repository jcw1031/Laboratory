package com.woopaca.laboratory.transaction.deadlock.repository;

import com.woopaca.laboratory.transaction.deadlock.entity.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

public class JpaCommentRepository {

    private final EntityManager entityManager;

    public JpaCommentRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void findByIdForUpdate() {
        Post post = entityManager.find(Post.class, 1L, LockModeType.PESSIMISTIC_WRITE);
    }

    public void findByIdForShare() {
        Post post = entityManager.find(Post.class, 1L, LockModeType.PESSIMISTIC_READ);
    }
}
