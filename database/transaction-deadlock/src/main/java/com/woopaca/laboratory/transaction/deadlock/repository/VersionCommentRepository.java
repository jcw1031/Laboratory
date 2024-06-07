package com.woopaca.laboratory.transaction.deadlock.repository;

import com.woopaca.laboratory.transaction.deadlock.entity.VersionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VersionCommentRepository extends JpaRepository<VersionComment, Long> {

    List<VersionComment> findByPostId(Long postId);
}
