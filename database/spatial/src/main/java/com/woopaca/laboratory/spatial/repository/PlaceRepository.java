package com.woopaca.laboratory.spatial.repository;

import com.woopaca.laboratory.spatial.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceCustomRepository {
}
