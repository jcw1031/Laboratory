package com.woopaca.laboratory.transaction.deadlock.repository;

import com.woopaca.laboratory.transaction.deadlock.entity.VersionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionCommentRepository extends JpaRepository<VersionComment, Long> {
}
