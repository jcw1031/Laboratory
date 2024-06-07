package com.woopaca.laboratory.transaction.deadlock.repository;

import com.woopaca.laboratory.transaction.deadlock.entity.NonRelationshipComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NonRelationshipCommentRepository extends JpaRepository<NonRelationshipComment, Long> {

    List<NonRelationshipComment> findByPostId(Long postId);
}
