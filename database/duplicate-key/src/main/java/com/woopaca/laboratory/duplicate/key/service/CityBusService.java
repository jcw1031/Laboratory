package com.woopaca.laboratory.duplicate.key.service;

import com.woopaca.laboratory.duplicate.key.entity.CityBus;
import com.woopaca.laboratory.duplicate.key.repository.CityBusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class CityBusService {

    private static final Random RANDOM = new Random();

    private final CityBusRepository cityBusRepository;

    public CityBusService(CityBusRepository cityBusRepository) {
        this.cityBusRepository = cityBusRepository;
    }

    public void updateCityBusArrivalTimes() {
        List<CityBus> cityBuses = cityBusRepository.findAll();
        cityBuses.forEach(this::updatePartiallyDuplicateTimes);
    }

    private void updatePartiallyDuplicateTimes(CityBus cityBus) {
        Set<LocalTime> newArrivalTimes = cityBus.getArrivalTimes()
                .stream()
                .map(arrivalTime -> {
                    int randomNumber = RANDOM.nextInt(2);
                    return arrivalTime.plusMinutes(randomNumber);
                })
                .collect(Collectors.toSet());
        cityBus.updateArrivalTimes(newArrivalTimes);
    }
}
