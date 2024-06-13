package com.woopaca.laboratory.spatial.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woopaca.laboratory.spatial.entity.Place;
import com.woopaca.laboratory.spatial.repository.PlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.booleanTemplate;
import static com.woopaca.laboratory.spatial.entity.QPlace.place;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class QueryDSLTest {

    final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    JPAQueryFactory queryFactory;

    @BeforeEach
    void setUp() {
        Point pointA = geometryFactory.createPoint(new Coordinate(127.2845, 37.5318));
        Point pointB = geometryFactory.createPoint(new Coordinate(127.4236, 37.6294));
        Point pointC = geometryFactory.createPoint(new Coordinate(127.3916, 37.5823));
        placeRepository.save(new Place("Place A", pointA));
        placeRepository.save(new Place("Place B", pointB));
        placeRepository.save(new Place("Place C", pointC));
    }

    @Test
    void fetchTest() {
        Point point = geometryFactory.createPoint(new Coordinate(127.3659, 37.5836));

        List<Place> places = queryFactory.selectFrom(place)
                .where(booleanTemplate(
                        "ST_Distance_Sphere({0}, {1}) <= {2}",
                        place.coordinates,
                        point,
                        2270))
                .fetch();
        log.info("places.size() = {}", places.size());

        places.forEach(place -> log.info("place.getName() = {}", place.getName()));
    }
}
