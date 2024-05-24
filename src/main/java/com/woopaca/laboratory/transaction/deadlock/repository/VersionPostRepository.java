package com.woopaca.laboratory.transaction.deadlock.repository;

import com.woopaca.laboratory.transaction.deadlock.entity.VersionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VersionPostRepository extends JpaRepository<VersionPost, Long> {

    Optional<VersionPost> findById(Long id);
}
