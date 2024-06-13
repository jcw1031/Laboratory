package com.woopaca.laboratory.spatial.service;

import com.woopaca.laboratory.spatial.repository.PlaceRepository;
import org.springframework.stereotype.Service;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

}
