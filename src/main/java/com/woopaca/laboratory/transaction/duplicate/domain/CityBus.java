package com.woopaca.laboratory.transaction.duplicate.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Table(name = "city_bus")
@Entity
public class CityBus extends BaseEntity {

    @Column(nullable = false)
    private String busNumber;

    @CollectionTable(name = "bus_time", joinColumns = @JoinColumn(name = "bus_id"))
    @Column(name = "arrival_time")
    @ElementCollection
    private Set<LocalTime> arrivalTimes = new HashSet<>();

    protected CityBus() {
        super(Domain.CITY_BUS);
    }

    public void updateArrivalTimes(Collection<LocalTime> arrivalTimes) {
        this.arrivalTimes.clear();
        this.arrivalTimes.addAll(arrivalTimes);
    }
}
