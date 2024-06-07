package com.woopaca.laboratory.transaction.duplicate.repository;

import com.woopaca.laboratory.transaction.duplicate.entity.CityBus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityBusRepository extends JpaRepository<CityBus, Long> {

    @EntityGraph(attributePaths = "arrivalTimes")
    Optional<CityBus> findById(Long id);

    @EntityGraph(attributePaths = "arrivalTimes")
    List<CityBus> findAll();

    List<CityBus> findByBusNumber(String busNumber);
}
