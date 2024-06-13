package com.woopaca.laboratory.spatial.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.woopaca.laboratory.spatial.entity.Place;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.woopaca.laboratory.spatial.entity.QPlace.place;

@Repository
public class PlaceCustomRepositoryImpl implements PlaceCustomRepository {

    private final JPAQueryFactory queryFactory;

    public PlaceCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Place> searchByPlaceName(String name) {
        return queryFactory.selectFrom(place)
                .where(place.name.containsIgnoreCase(name))
                .fetch();
    }

}
