package com.woopaca.laboratory.spatial.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point coordinates;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point coordinatesWithIndex;

    public Place() {
    }

    @Builder
    public Place(String name, Point coordinates, Point coordinatesWithIndex) {
        this.name = name;
        this.coordinates = coordinates;
        this.coordinatesWithIndex = coordinatesWithIndex;
    }

}
