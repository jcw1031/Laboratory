package com.woopaca.laboratory.spatial.repository;

import com.woopaca.laboratory.spatial.entity.Place;

import java.util.List;

public interface PlaceCustomRepository {

    List<Place> searchByPlaceName(String name);

}
