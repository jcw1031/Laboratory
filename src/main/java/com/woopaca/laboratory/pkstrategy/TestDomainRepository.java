package com.woopaca.laboratory.pkstrategy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestDomainRepository extends JpaRepository<TestDomain, Long> {
}
