package com.woopaca.laboratory.spatial.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woopaca.laboratory.spatial.entity.Place;
import com.woopaca.laboratory.spatial.repository.PlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.List;

import static com.woopaca.laboratory.spatial.entity.QPlace.place;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class QueryDSLTest {

    final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    final CoordinateSequenceFactory coordinateSequenceFactory = geometryFactory.getCoordinateSequenceFactory();

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
    void distanceSearchTest() {
        Point point = geometryFactory.createPoint(new Coordinate(127.3659, 37.5836));

        NumberExpression<Double> distanceSphere = Expressions.numberTemplate(
                Double.class, "ST_Distance_Sphere({0}, {1})", place.coordinates, point
        );

        List<Place> places = queryFactory.selectFrom(place)
                .where(distanceSphere.loe(2300))
                .orderBy(distanceSphere.asc())
                .fetch();
        log.info("places.size() = {}", places.size());
        places.forEach(place -> log.info("place.getName() = {}", place.getName()));
    }

    @Test
    void boundSearchTest() {
        Polygon polygon = geometryFactory.createPolygon(new Coordinate[]{
                // 37.11111, 37.99999, 127.11111, 127.99999
                new Coordinate(127.11111, 37.11111),
                new Coordinate(127.99999, 37.11111),
                new Coordinate(127.99999, 37.99999),
                new Coordinate(127.11111, 37.99999),
                new Coordinate(127.11111, 37.11111)
        });
        BooleanExpression contains = Expressions.booleanTemplate(
                "ST_Contains({0}, {1})", polygon, place.coordinates
        );

        List<Place> places = queryFactory.selectFrom(place)
                .where(contains)
                .fetch();
        log.info("places.size() = {}", places.size());
        places.forEach(place -> log.info("place.getName() = {}", place.getName()));
    }

}
