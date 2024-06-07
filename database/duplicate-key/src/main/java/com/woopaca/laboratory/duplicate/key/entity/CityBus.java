package com.woopaca.laboratory.duplicate.key.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@ToString
@Getter
@Table(name = "city_bus")
@Entity
public class CityBus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String busNumber;

    @CollectionTable(name = "bus_time", joinColumns = @JoinColumn(name = "bus_id"))
    @Column(name = "arrival_time")
    @ElementCollection
    private Set<LocalTime> arrivalTimes = new HashSet<>();

    protected CityBus() {
    }

    public CityBus(String busNumber) {
        this.busNumber = busNumber;
    }

    public void updateArrivalTimes(Collection<LocalTime> arrivalTimes) {
        this.arrivalTimes.clear();
        this.arrivalTimes.addAll(arrivalTimes);
    }
}
