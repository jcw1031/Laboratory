package com.woopaca.laboratory.transaction.duplicate.repository;

import com.woopaca.laboratory.transaction.duplicate.domain.CityBus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityBusRepository extends JpaRepository<CityBus, String> {

    @EntityGraph(attributePaths = "arrivalTimes")
    List<CityBus> findAll();
}
